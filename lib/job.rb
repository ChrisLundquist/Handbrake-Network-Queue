# Represents a job from/on the Queue
require 'rexml/document'
class Job
    attr_reader :id, # The ID of the job from the server
        :query,      # The command line that would have been run on the server
        :source,     # The location of the inpurt file on the server
        :destination # The location of the desired destination on the server

    # We expect to be passed part of an XML Document. Specifically 
    # the things between <Job> and </Job>
    def initialize( job_xml )
        document = REXML::Document.new(job_xml)

    end
end
