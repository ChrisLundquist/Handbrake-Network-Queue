
require 'socket'    # So we can connect to the server
require './lib/job' # Check out Job objects from the servr
require './lib/command.rb'
require 'yaml'

HOST = "Mjolnir"
PORT = 4444

class Client
    attr_reader :server, # Our Socket to the server
        :job,     # The job we are responsible for
        :host,    # The hostname of the server we are connecting to
        :port     # the port on the server we are connecting to

    def initialize(host = HOST, port = PORT)
        @host = host
        @port = port
    end

    # Main function to be called
    def run
        connect()
        get_job()
        disconnect()
        connect()
        do_job()
        disconnect()
        connect()
        complete_job()
        disconnect()
    end

    private

    def disconnect
        @server.close
    end

    def connect(host = HOST, port = PORT)
        @server = TCPSocket.open(host,port)
        raise "Unable to connect to #{host}:#{port}" unless @server
    end

    def get_job()
        puts "getting job with command: #{Command::GET_JOB}"
        @server.write(Command::GET_JOB)
        f = File.new("test.job","w")
        response = @server.recvmsg.first
        response.chomp!
        @job = YAML.load(response)
        f.write(@job.to_yaml)
        f.close
    end

    def do_job()
        puts "doing job"
        #TODO 
    end

    # Write the command
    # Then the Job ID
    def complete_job()
        puts "completing job"
        @server.write(Command::COMPLETE_JOB)
        @server.write(@job.id)
    end
end
