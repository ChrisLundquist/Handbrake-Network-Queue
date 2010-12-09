#!/usr/bin/env ruby

require 'socket'  # TCPServer
require './lib/job'     # Job
require 'rexml/document' # REXML::Document to parse XML without gems

PORT = 4444
class Server
    attr_reader :server, # The TCPSocket for the server
      :queue_file,       # The Queue file we are responsible for completing
      :job_queue         # Job Objects generated from the queue_file

    def initialize(queue_file, port = PORT)
        case queue_file
        when File
            new_from_file(queue_file)
        when String
            new_from_path(queue_file)
        else
            raise "Unsupported construction from class #{queue_file.class}
            expected File or String of the path to the file"
        end

        @server = TCPServer.new(port)
        parse_queue_file()
    end

    # XXX TODO XXX check that the client is on the local subnet. E.G. 10. address or 192.168 address
    def serve_client
        client = @server.accept
        client.puts(@queue_file.read)
        @queue_file.rewind
        client.close
    end

    # The main running loop of the server.
    # TODO Make this forking so we can serve multiple clients
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

    # Turns our queue file generated by Handbrake into Job objects
    def parse_queue_file
        document = REXML::Document.new(@queue_file.read)
        @queue_file.rewind
        # Populate our job queue from the queue_file we were given
        document.elements.each("ArrayOfJob/Job") do |job|
            @job_queue.push(Job.new(job))  
        end
    end
end
