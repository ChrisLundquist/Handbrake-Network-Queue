#!/usr/bin/env ruby

require './lib/client.rb'

#TODO Parse ARGV? or should the client Class handle that?
host = ARGV.shift
port = 4444
port ||= ARGV.shift
Client.new(host,port).run
