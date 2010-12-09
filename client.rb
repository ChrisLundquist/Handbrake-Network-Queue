#!/usr/bin/env ruby

require 'socket'

HOST = "Mjolnir"
PORT = 4444


def main
    server = TCPSocket.open(HOST,PORT)
    f = File.new("test.queue","w")
    f.write(server.read)
    server.close
    f.close
end
main
