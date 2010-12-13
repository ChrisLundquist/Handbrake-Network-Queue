class ServerThread
    def initialize(client, queue)
        @client = client
        @queue = queue
    end

    def serve_client
        command = @client.recvmsg.first
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
            @client.write(Command::UNKNOWN)
        end
        @client.close
    end
    alias :run :serve_client

    def get_job
        puts "client has requested a job"
        job = @queue.next_job()
        if job
            @client.write(job.to_yaml)
        else
            @client.write(Command::NO_JOB)
            STDERR.puts "No new jobs left..."
        end
    end

    def checkout_job
        id = @client.recvmsg.first
        queue.checkout(id)
        puts "client has checked out job id #{id}"
    end

    def complete_job
        id = @client.recvmsg.first
        puts "client has completed a job id: #{id}"
        @queue.complete(id)
    end

end
