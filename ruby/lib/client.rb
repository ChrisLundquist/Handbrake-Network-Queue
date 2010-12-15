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
        f = File.new("test.job","w")
        response = @server.gets
        if response.chomp == Command::NO_JOB
            STDERR.puts "No jobs on remote host available"
            disconnect()
            return false
        else
            response += @server.read
            @job = YAML.load(response)
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

        num_files = @server.gets.to_i
        num_files.times do |file_number|
            FileTransfer.recv(@server)
        end

        disconnect()
    end

    def do_job()
        puts "Doing job id #{@job.id}"
        #TODO 
    end

    # Write the command
    # Then the Job ID
    def complete_job()
        puts "Completing job id #{@job.id}"
        connect()
        @server.puts(Command::COMPLETE_JOB)
        @server.puts(@job.id)
        disconnect()
    end
end
