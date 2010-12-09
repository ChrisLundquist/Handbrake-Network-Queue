
require 'socket'

HOST = "Mjolnir"
PORT = 4444

class Client
    attr_reader :server
    def connect
        @server = TCPSocket.open(HOST,PORT)
        raise "Unable to connect to #{HOST}:#{PORT}" unless @server
    end

    def get_job
        f = File.new("test.queue","w")
        f.write(server.read)
        @server.close
        f.close
    end

    def do_job()
    end

    def run
        connect()
        get_job()
        do_job()
    end

end
