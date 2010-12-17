# vi: et sw=4
require 'rexml/document'
# Represents a job from/on the Queue
class Job
    attr_reader :id, # The ID of the job from the server
        :query,      # The command line that would have been run on the server
        :source,     # The location of the inpurt file on the server
        :destination,# The location of the desired destination on the server
        :status,     # The status of the job. New / Checked Out / Complete / Canceled etc.
        :xml,
        :checked_out_at

    ID_INDEX = 1
    QUERY_INDEX = 3
    SOURCE_INDEX = 7
    DESTINATION_INDEX = 9

    NEW = "New"
    CHECKED_OUT = "Checked_Out"
    COMPLETE = "Complete"
    CANCELED = "Canceled"

    SOURCE_TOKEN = "__SOURCE__"
    DESTINATION_TOKEN = "__DESTINATION__"

    def self.send(socket,job)
        # ID
        socket.puts job.id
        # Query
        socket.puts job.query
        # Source
        socket.puts job.source
        # Dest
        socket.puts job.destination
    end

    def self.recv(socket)
        # ID
        id = socket.gets.chomp
        # Query
        query = socket.gets.chomp
        # Source
        source = socket.gets.chomp
        # Dest
        dest = socket.gets.chomp
        job = Job.new

        # HACK FIXME get rid of instance_eval
        #NOTE Inspect puts "" around our stuff since they are string class.
        #     otherwise its a bunch of nonsensical literals.
        job.instance_eval(" 
        @id = #{id}
        @query = #{query.inspect}
        @source = #{source.inspect}
        @destination = #{dest.inspect}" 
                         )
        job
    end

    def initialize(job_xml = nil)
        # Return a bare object if they don't get us anything
        return unless job_xml

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
        tokenize_query()

        # We have to nuke the XML because it screws up serialization when
        # we try to send it to the client. because its REXML::Element nested
        # doom crap.
        @xml = nil
    end

    def prepare
        # The source directory will be relative to Dir.pwd on the client
        @source = @source.split("/").last
        @destination = @destination.split("/").last
        detokenize_query()
    end

    def files
        get_files(@source)
    end

    def relative_files
        # Save where we are
        pwd = Dir.pwd
        # Go where we need to be
        Dir.chdir(@source)
        # Find the files below this
        files = get_files(".")
        # Go back to where we were
        Dir.chdir(pwd)
        # Return our files relative to @souce
        files
    end

    def relative_files_with_source_dir
        pwd = Dir.pwd
        Dir.chdir(@source)
        # We do it this way so it works on all platforms. @source might have \ instead of /
        folder = Dir.pwd.split("/").last + "/"
        Dir.chdir(pwd)

        relative_files.map do |file|
            folder + file.sub("./","")
        end
    end

    def dirs
        # if the paths are in ascending order you can't try to make /foo/bar before /foo/
        get_dirs(@source).sort_by{ |i| i.length }
    end

    def relative_dirs
        # Save where we are
        pwd = Dir.pwd
        # Go where we need to be
        Dir.chdir(@source)
        # Find the dirs below this
        dirs = get_dirs(".")
        # Go back to where we were
        Dir.chdir(pwd)
        # Return our dirs relative to @souce
        # if the paths are in ascending order you can't try to make /foo/bar before /foo/
        dirs.sort_by{ |i| i.length }
    end

    def relative_dirs_with_source_dir
        pwd = Dir.pwd
        Dir.chdir(@source)
        # We do it this way so it works on all platforms. @source might have \ instead of /
        folder = Dir.pwd.split("/").last + "/"
        Dir.chdir(pwd)

        dirs = relative_dirs.map do |dir|
            folder + dir.sub("./","")
        end
        # Add the source dir
        dirs.push(folder)
        # if the paths are in ascending order you can't try to make /foo/bar before /foo/
        dirs.sort_by{ |i| i.length }
    end

    def new?
        return @status == NEW
    end

    def checkout!
        raise "Invalid checkout of complete job" if complete?
        @checked_out_at = Time.now
        @status = CHECKED_OUT
        self
    end

    def checked_out?
        @status == CHECKED_OUT
    end

    def complete?
        @status == COMPLETE
    end

    def complete!
        @status = COMPLETE
        self
    end

    private
    def get_id_from_xml
        @xml[ID_INDEX].text.to_i
    end

    def get_query_from_xml
        @xml[QUERY_INDEX].text.to_s
    end

    def get_source_from_xml
        @xml[SOURCE_INDEX].text.to_s
    end

    def get_destination_from_xml
        @xml[DESTINATION_INDEX].text.to_s
    end

    # Recursively gets all the dir paths from a root
    def get_dirs(dir_path)
        dirs = Array.new
        Dir[dir_path + "/*"].each do |i|
            if Dir.exists?(i)
                dirs.push(i) if File.directory?(i)
                # Recursive
                dirs.push(get_dirs(i)) 
            end
        end
        dirs.flatten!
        dirs.compact!
        dirs
    end

    def get_files(dir_path)
        files = Array.new
        # For all the directories below me
        get_dirs(dir_path).each do |dir|
            # Grab all the files
            Dir[dir + "/*"].each do |i|
                if File.exists?(i)
                    files.push(i) unless File.directory?(i)
                end
            end
        end
        files
    end

    # We tokenize this because the paths on the remote host
    # are going to be different
    def tokenize_query
        @query.sub!(@source,SOURCE_TOKEN)
        @query.sub!(@destination,DESTINATION_TOKEN)
    end

    # Inverse transform. Hopefully @source and @destination
    # will have been replaced for the local machine
    def detokenize_query
        @query.sub!(SOURCE_TOKEN,@source)
        @query.sub!(DESTINATION_TOKEN,@destination)
    end
end
