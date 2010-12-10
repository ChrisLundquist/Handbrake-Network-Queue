require 'rexml/document'
# Represents a job from/on the Queue
class Job
    attr_reader :id, # The ID of the job from the server
        :query,      # The command line that would have been run on the server
        :source,     # The location of the inpurt file on the server
        :destination,# The location of the desired destination on the server
        :status,     # The status of the job. New / Checked Out / Complete / Canceled etc.
        :xml

    ID_INDEX = 1
    QUERY_INDEX = 3
    SOURCE_INDEX = 7
    DESTINATION_INDEX = 9

    # Numbers are arbitrary
    NEW = 0x42
    CHECKED_OUT = 0x43
    COMPLETE = 0x44
    CANCELED = 0x45
    def initialize(job_xml)
        @xml = case job_xml
               when String
                   REXML::Document.new(job_xml)
               when REXML::Element
                   job_xml
               else
                   raise "Unsupport constructor from class #{job_xml.class}"
               end
        @status = NEW
        @id = get_id_from_xml()
        @query = get_query_from_xml()
        @source = get_source_from_xml()
        @destination = get_destination_from_xml()
    end

    def new?
        return @status == NEW
    end

    def checkout!
      raise "Invalid checkout of complete job" if complete?
      @status = CHECKOUT_OUT
    end

    def complete?
      return @status == COMPLETE
    end

    private
    def get_id_from_xml
        @xml[ID_INDEX].text
    end

    def get_query_from_xml
        @xml[QUERY_INDEX].text
    end

    def get_source_from_xml
        @xml[SOURCE_INDEX].text
    end

    def get_destination_from_xml
        @xml[DESTINATION_INDEX].text
    end
end
