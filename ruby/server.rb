#!/usr/bin/env ruby

require './lib/server.rb'

#TODO Handle ARGV? or should Server class?

Server.new("./test/test.queue").run
