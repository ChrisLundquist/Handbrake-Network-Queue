#!/usr/bin/env ruby

require './lib/server.rb'

#TODO Handle ARGV? or should Server class?
queue_file_path = ARGV.shift
Server.new(queue_file_path).run
