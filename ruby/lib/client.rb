# vi: et sw=4
require 'socket'    # So we can connect to the server
require './lib/job' # Check out Job objects from the servr
require './lib/command'
require './lib/file_transfer'
require 'yaml'

PORT = 4444

class Client
    attr_reader :server, # Our Socket to the server
        :job,     # The job we are responsible for
        :host,    # The hostname of the server we are connecting to
        :port     # the port on the server we are connecting to

    def initialize(host, port = PORT)
        @host = host
        @port = port
    end

    # Main function to be called
    def run
        while (get_job() ) do
            checkout_job()
            do_job()
            complete_job()
        end
    end

    private

    def disconnect
        @server.close
    end

    def connect()
        @server = TCPSocket.open(@host, @port)
        # This throws an exception on failure as desired/expected
    end

    def get_job()
        connect()
        @server.puts(Command::GET_JOB)
        response = @server.gets.chomp
        if response == Command::NO_JOB
            STDERR.puts "No jobs on remote host available"
            disconnect()
            return false
        else
            @job = Job.recv(@server)
            # For to real reason we print this to a file
            f = File.new("current.job","w")
            f.write(@job.to_yaml)
            f.close
            disconnect()
        end
        return true
    end

    def checkout_job()
        puts "Checking out job id #{@job.id}"
        connect()
        @server.puts(Command::CHECKOUT_JOB)
        @server.puts(@job.id)

        # Make the relative directories for the files
        FileTransfer.make_dirs(@server)
        # Get the files which should be in the relative directory we just made
        FileTransfer.recv(@server)

        disconnect()
    end

    def do_job()
        puts "Doing job id #{@job.id}"
        @job.prepare
        # XXX Be less hacky
        puts "HandBrakeCLI #{@job.query}"
        `HandBrakeCLI #{@job.query}`
    end

    # Write the command
    # Then the Job ID
    def complete_job()
        puts "Completing job id #{@job.id}"
        connect()
        @server.puts(Command::COMPLETE_JOB)
        @server.puts(@job.id)
        # Send the encoded file
        FileTransfer.send(@server,@job.destination)
        disconnect()
    end
end
