#!/usr/bin/env ruby
# vi: et sw=4

require 'socket'  # TCPServer
require './lib/job'      # Job
require './lib/job_queue' # The control structure for the jobs.
require './lib/command'
require './lib/server_thread'

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
    # The main running loop of the server.
    def run
        loop do
            Thread.start(server.accept) do |client|
                begin
                    ServerThread.new(client,@job_queue).run
                rescue Exception => e
                    STDERR.puts "A server thread has encountered an exception:"
                    STDERR.puts "--------------------------------"
                    STDERR.puts e.message
                    STDERR.puts e.backtrace
                    STDERR.puts "--------------------------------"
                end
            end
        end
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
        @job_queue = JobQueue.new(@queue_file)
        puts "#{@job_queue.length} jobs found"
        @queue_file.close
    end
end
