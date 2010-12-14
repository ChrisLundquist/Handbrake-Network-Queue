class FileTransfer
    #DEFAULT_PORT = 5555
    def self.send(file, socket)
        # Make sure it is a file and not path
        file_path = File.open(file) if file.is_a?(String)
        puts "Sending file...#{read_file_name(file)}"

        # Read the file name
        socket.puts(read_file_name(file))

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

        # TODO close the connection????
        # If we are piggy backing this might be suprising for our users
    end

    def self.recv(socket)
        # Read the file name
        file_name = socket.gets
        puts "Receiving file...#{file_name}"
        # TODO Test we don't have a file by the same name
        file = File.open(file_name,"w")

        # Read the size of the incomming file
        size = socket.gets

        block_size = socket.gets

        # Integer math intentional
        (size / block_size).times do
            # Read a block at a time
            file.write(socket.read(block_size))
        end

        # Read the rest of the file
        file.write(socket.read( size % block_size ))
        
        # TODO close the connection????
        # If we are piggy backing this might be suprising for our users
    end

    private
    def self.read_file_name(file)
        file.path.split("/").last
    end

    def self.read_file_size(file)
        file.size
    end

    def self.read_file_block_size(file)
        file.stat.blksize
    end
end
