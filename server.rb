#!/usr/bin/env ruby

require 'socket'

puts RUBY_VERSION
PORT = 4444

def main
    server = TCPServer.new(PORT)
    f = File.open("test.queue")
    loop{
        client = server.accept
        client.puts(f.read)
        f.rewind
        client.close
    }
end
main()
