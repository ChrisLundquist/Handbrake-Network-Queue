require 'rexml/document'
require './lib/job'
class JobQueue
    attr_reader :jobs, :queue_file

    def initialize(queue_file)
        @jobs = Array.new
        @queue_file = queue_file
        case @queue_file
        when String
            @queue_file = File.open(queue_file)
        when File
        else
            raise "Unhandled type in initialization #{queue_file.class}"
        end
        parse_queue_file()
    end

    def next_job
        # look for a new job
        job = @jobs.select { |i| i.new? }.first
        
        # TODO: look for a checked out job that hasn't been completed
        # NOTE: We have to handle what happens when multiple people turn in the same job
        #job ||= @jobs.select { |i| i.checked_out? }.sort_by { |i|  i.checked_out_at }.first
        return job
    end

    def checkout(id)
      find_job_by_id(id).checkout!
    end

    def complete(id)
      find_job_by_id(id).complete!
    end

    def length
        return jobs.length
    end

    private

    def find_job_by_id(id)
      id = id.to_i
      job = @jobs.select { |i| i.id == id }.first
      return job if job
      STDERR.puts("Unable to locate job with id #{id}. have the following jobs")
      @jobs.each do |i| puts i.inspect end
    end

    def parse_queue_file
        document = REXML::Document.new(@queue_file.read)
        # Populate our job queue from the queue_file we were given
        document.elements.each("ArrayOfJob/Job") do |job_xml|
            @jobs.push(Job.new(job_xml))
        end
    end
end
