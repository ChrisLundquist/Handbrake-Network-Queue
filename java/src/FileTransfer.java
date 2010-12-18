
import java.io.*;

import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileTransfer{
    static public final String  DECLINE = "Decline";
    static public final String ACCEPT = "Accept";
    static public final String CACHED = "Cached";
    static public final String EXISTS = "Exists";


    public static void send(Socket socket, String[] file_paths){
    }

    public static void recv(Socket socket) {
        try {
            PrintWriter server = new PrintWriter(socket.getOutputStream());
            int numFiles = Integer.parseInt(readLine(socket));
            System.out.println("Receiving " + numFiles + " files...");

            for( int i = 0; i < numFiles; i++){
                // Get the file's name
                String fileName = readLine(socket);
                System.out.println("Receiving file " + fileName);
                File file = new File(fileName);

                // Check if we have a file by the same name here
                if(file.exists()){
                    System.out.println("File " + file + " already exists");
                    server.println(EXISTS);
                    server.flush();
                    try {
                        // Get what the server computes for the digest of the file
                        String hexdigest = readLine(socket);

                        MessageDigest md = MessageDigest.getInstance("MD5");
                        // TODO see if we can md5 the file we have.
                        if(MessageDigest.isEqual(hexdigest.getBytes(), md.digest())){
                            System.out.println("Already have file " + fileName + " Cached...skipping");
                            server.println(CACHED);
                            server.flush();
                            continue;
                        }
                    } catch (NoSuchAlgorithmException e) {
                        System.err.println("Your machine doesn't support MD5. Thats amazing.");
                    }
                }
                // Accept the file
                server.println(ACCEPT);
                server.flush();
                BufferedOutputStream binWriter = new BufferedOutputStream(new FileOutputStream(file));
                // Read this files size
                long fileSize = Integer.parseInt(readLine(socket));

                // Read the block size they are going to use
                int blockSize = Integer.parseInt(readLine(socket));
                byte[] buffer = new byte[blockSize];

                long bytesLeft = fileSize;
                int read = 0;
                
                while(bytesLeft != 0){
                    // Read the min of the bytesLeft and blocksize
                    read = socket.getInputStream().read(buffer, 0, bytesLeft < blockSize ? (int)bytesLeft : blockSize );
                    binWriter.write(buffer,0,read);
                    bytesLeft -= read;
                    //System.out.println(bytesLeft + " bytes left");
                }
                binWriter.flush();
                binWriter.close();
                System.out.println("Done with " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Socket I/O Error when transfering file ");
        }
    }

    public static void sendDirs(Socket socket, String[] dirNames){

    }

    public static void makeDirs(Socket socket){
        try {
            // Read how many dirs need to be made
            int numDirs = Integer.parseInt(readLine(socket));

            //Make each directory
            for(int i = 0; i < numDirs; i++){
                // Read the dir name on the server
                String dirName = readLine(socket);
                System.out.println("Making directory " + dirName);

                File dir = new File(dirName);
                if(dir.exists()){
                    // Do nothing
                } else {
                    // Make the directory
                    if(dir.mkdir() == false){
                        System.err.println("Unable to make dir " + dirName);
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Unable to parse the number of Dirs to create");
            System.err.println(e);
        }
    }
    private static String readLine(Socket socket){
        String line = new String();
        int c;
        
        try {
            while((c = socket.getInputStream().read()) != '\n'){
                line += (char) c;
            }
        } catch (IOException e) {
            System.err.println("Error reading line from stream");
            System.exit(-14);
        }

        // We may have a trailing return
        line = line.trim();

        //System.out.println("Read Line " + line);
        return line;
    }
}
