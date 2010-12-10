
require 'socket'    # So we can connect to the server
require './lib/job' # Check out Job objects from the servr
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
        do_job()
    end

    private

    def connect(host = HOST, port = PORT)
        @server = TCPSocket.open(host,port)
        raise "Unable to connect to #{host}:#{port}" unless @server
    end

    def get_job
        f = File.new("test.job","w")
        response = server.read
        puts response.inspect
        job = YAML.load(response)
        f.write(job.to_yaml)
        f.close
        @server.close
    end

    def do_job()
    end
end
