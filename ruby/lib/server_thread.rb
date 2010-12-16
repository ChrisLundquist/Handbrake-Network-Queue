# vi: et sw=4
require "./lib/file_transfer"
class ServerThread
    def initialize(client, queue)
        @client = client
        @queue = queue
    end

    def serve_client
        command = @client.gets.chomp
        puts command
        case command 
        when Command::GET_JOB
            get_job()
        when Command::CHECKOUT_JOB
            checkout_job()
        when Command::COMPLETE_JOB
            complete_job()
        when ""
            # They closed the socket
        else
            STDERR.puts "unknown command: #{command.inspect}"
            @client.puts(Command::UNKNOWN)
        end
    ensure
        @client.close
    end
    alias :run :serve_client

    private
    def get_job
        puts "client has requested a job"
        job = @queue.next_job()
        if job
            @client.puts(Command::HAVE_JOB)
            Job.send(@client,job)
            puts "Sent job ID #{job.id}"
        else
            @client.puts(Command::NO_JOB)
            STDERR.puts "No new jobs left..."
        end
    end

    def checkout_job
        id = read_id()
        job = @queue.checkout(id)
        puts "client has checked out job id #{id}"

        # Send them the directory structure
        FileTransfer.send_dirs(@client, job.relative_dirs_with_source_dir)

        # Save where we are
        pwd = Dir.pwd
        # Go to the source folder so we can send relative paths
        Dir.chdir(job.source + "/..")

        # Get the files required
        FileTransfer.send(@client,job.relative_files_with_source_dir)

        # Go back to where we were
        Dir.chdir(pwd)
    end

    def complete_job
        id = read_id()
        puts "client has completed a job id: #{id}"
        FileTransfer.recv(@client)
        @queue.complete(id)
    end

    def read_id
        @client.gets
    end
end
