
import java.io.*;

import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

public class FileTransfer{
    static public final String DECLINE = "Decline";
    static public final String ACCEPT = "Accept";
    static public final String CACHED = "Cached";
    static public final String EXISTS = "Exists";

    public static void send(Socket socket, Vector<String> vector){
        System.out.println("Sending " + vector.size() + " files");
        try {
            PrintWriter server = new PrintWriter(socket.getOutputStream());
            // Tell them how many files
            server.println(vector.size());
            for(String fileName : vector){
                server.println(fileName);
                server.flush();

                String response = readLine(socket);
                File file = new File(fileName);
                if(response.equals(EXISTS)){
                    server.println(getMD5Digest(file));
                    server.flush();
                    response = readLine(socket);

                    if(response.equals(CACHED) || response.equals(DECLINE))
                        continue; // They don't want or need this file, go to the next
                }else if(response.equals(ACCEPT)){
                    // send the file as normal
                }

                long fileSize = file.length();
                long bytesRead = 0;
                int read = 0;
                byte[] buffer = new byte[4096];

                server.println(fileSize);
                server.println(buffer.length);
                server.flush();

                System.out.println("Transfering " + fileName);
                BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
                while(bytesRead < fileSize){
                    read = reader.read(buffer);
                    socket.getOutputStream().write(buffer,0,read);
                    bytesRead += read;
                }
                socket.getOutputStream().flush();
                System.out.println("Done Transfering " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    // Get what the server computes for the digest of the file
                    String hexDigest = readLine(socket);
                    String ourHexDigest = getMD5Digest(file);

                    if(hexDigest.equals(ourHexDigest)){
                        System.out.println("Already have file " + fileName + " Cached...skipping");
                        server.println(CACHED);
                        server.flush();
                        continue;
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

    private static String getMD5Digest(File file) {
        BufferedInputStream reader = null;
        String hexDigest = new String();
        try {
            reader = new BufferedInputStream( new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] buffer = new byte[4096];
        long fileLength = file.length();
        long bytesLeft = fileLength;
        int  read = 0;

        //Read our file into the md buffer
        while(bytesLeft > 0){
            try {
                read = reader.read(buffer,0, bytesLeft < buffer.length ? (int)bytesLeft : buffer.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            md.update(buffer,0,read);
            bytesLeft -= read;
        }
        byte[] digest = md.digest();
        for (int i = 0; i < digest.length;i++) {
            hexDigest += String.format("%02x" ,0xFF & digest[i]);
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hexDigest;
    }

    public static void sendDirs(Socket socket, Vector<String> vector){
        PrintWriter out = null;
        try {
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Cannot get output stream of socket");
            System.exit(-14);
        }
        // Tell the client how many dirs we are going to send
        out.println(vector.size());
        for(String dirName : vector){
            System.out.println("sending dir " + dirName);
            // Print each dirname to the client
            out.println(dirName);
        }   
        out.flush();
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
    public static String readLine(Socket socket){
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
