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
        job = @jobs.select { |i| i.new? }.first
        if job
            job.checkout!
            return job
        end
        return nil
    end

    def complete(id)
        id = id.to_i
        @jobs.select { |i| i.id == id }.first.complete!
    end

    def length
        return jobs.length
    end

    private
    def parse_queue_file
        document = REXML::Document.new(@queue_file.read)
        # Populate our job queue from the queue_file we were given
        document.elements.each("ArrayOfJob/Job") do |job_xml|
            @jobs.push(Job.new(job_xml))
        end
    end
end
