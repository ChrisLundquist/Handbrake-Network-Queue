#!/usr/bin/env ruby

require 'socket'

PORT = 4444
class Server
    attr_reader :server, :queue_file

    def initialize(queue_file)
        case queue_file
        when File
            new_from_file(queue_file)
        when String
            new_from_path(queue_file)
        else
            raise "Unsupported construction from class #{queue_file.class}
            expected File or String of the path to the file"
        end

        @server = TCPServer.new(PORT)
    end

    def serve_client
        client = @server.accept
        client.puts(@queue_file.read)
        @queue_file.rewind
        client.close
    end

    def run
        loop {
            serve_client()
        }
    end

    private
    def new_from_path(path)
        @queue_file = File.open(path)
        raise "Unable to open path: #{path}" unless @queue_file
    end

    def new_from_file
        # Nothing to do for now
    end

end
