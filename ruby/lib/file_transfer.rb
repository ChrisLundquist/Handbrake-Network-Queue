# vi: et sw=4
class FileTransfer
    DECLINE = "Decline"
    ACCEPT = "Accept"

    def self.send(socket,file_or_path)
        # Make sure it is a file and not path
        file = case file_or_path
               when String
                   file = File.open(file_or_path,"rb:binary")
               when File
                   file_or_path
               end

        # Read the file name
        file_name = read_file_name(file)
        socket.puts(file_name)

        # If they don't want the file we shouldn't send it
        if socket.gets.chomp == DECLINE
            puts "Client declined file transfer of #{file_name}"
            return
        end

        puts "Sending file...#{file_name}"

        # Send the size of the file
        size = read_file_size(file)
        socket.puts(size)

        # Send the block size we are going to use
        block_size = read_file_block_size(file)
        socket.puts(block_size)

        # Integer math intentional
        (size / block_size).times do
            # Send the file a block at a time
            socket.write(file.read(block_size))
        end

        # Send the rest of the file
        socket.write(file.read(size % block_size))
    end

    def self.recv(socket)
        # Read the file name VERY important to chomp it. Otherwise your filename has \n
        file_name = socket.gets.chomp

        # Test we don't have a file by the same name
        if File::exists?(file_name)
            puts "Overwrite '#{file_name}'?(y/n)" 
            if ["n","N","no","No","NO"].include?(gets.chomp)
                # We like ours better
                socket.puts(DECLINE)
                return
            end
        end
        # We accept the file.
        socket.puts(ACCEPT)
        puts "Receiving file...#{file_name}"
        file = File.open(file_name,"wb:binary")

        # Read the size of the incomming file
        size = socket.gets.to_i

        block_size = socket.gets.to_i
        buffer = nil
        begin
            # Integer math intentional
            (size / block_size).times do
                # Read a block at a time
                buffer = socket.read(block_size)
                file.write(buffer)
            end

            # Read the rest of the file
            file.write(socket.read( size % block_size ))
        rescue Exception => e
            STDERR.puts e.message
            STDERR.puts e.backtrace
            STDERR.puts buffer.inspect
            socket.close
        end
    end

    # Sends the directory structure
    def self.send_dirs(socket,dirs)
        # Tell them how many
        socket.puts(dirs.length)
        dirs.each do |dir|
            socket.puts(dir)
        end
    end

    # Makes the directory structure
    def self.make_dirs(socket)
        # See how many we are going to do
        num_dirs = socket.gets.to_i

        num_dirs.times do |dir_num|
            # Read the dir name
            dir_name = socket.gets.chomp
            puts "Making dir #{dir_name}"
            if Dir.exists?(dir_name)
                # nothing to do
            else
                Dir.mkdir(dir_name)
            end
        end
    end

    private
    def self.read_file_name(file)
        file.path
    end

    def self.read_file_size(file)
        file.stat.size
    end

    def self.read_file_block_size(file)
        file.stat.blksize
    end
end
