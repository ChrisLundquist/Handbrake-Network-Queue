import java.io.BufferedReader;
import java.io.File;
//import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileTransfer{
    static public final String CACHED = "Cached";
    static public final String ACCEPT = "Accept";
    static public final String DECLINE = "Decline";
    static public final String EXISTS = "Exists";

    public static void send(Socket socket, String[] file_paths){
    }

    public static void recv(Socket socket){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            int numFiles = Integer.parseInt(in.readLine());

            for( int i = 0; i < numFiles; i ++){
                // Get the file's name
                String fileName = in.readLine();
                System.out.println("Receiving file " + fileName);
                File file = new File(fileName);

                // Check if we have a file by the same name here
                if(file.exists()){
                    out.println(EXISTS);
                    try {
                        //FileReader fileReader = new FileReader(fileName);
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        md.digest();
                    } catch (NoSuchAlgorithmException e) {
                        System.err.println("Your machine doesn't support MD5. Thats amazing.");
                    }

                    // Followed by the file's MD5
                    //String fileHash = in.readLine();
                }
                // Accept the file
                out.println(ACCEPT);
                FileWriter fileWriter = new FileWriter(file);

                // Read this files size
                long fileSize = Integer.parseInt(in.readLine());

                // Read the block size they are going to use
                int blockSize = Integer.parseInt(in.readLine());
                byte[] buffer = new byte[blockSize];

                // Bytes "red"
                long bytesRead = fileSize;
                int read = 0;
                while(bytesRead > fileSize){
                    read = socket.getInputStream().read(buffer);
                    // Java is retarded and reading and writing operate with
                    // fundamentally different types. So we write a String of
                    // binary data.
                    fileWriter.write(new String(buffer));
                    bytesRead += read;
                }

            }
        } catch (IOException e) {
            System.err.println("Socket I/O Error when transfering file ");
        }
    }

    public static void sendDirs(Socket socket, String[] dirNames){

    }

    public static void makeDirs(Socket socket){
        try {
            BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()));

            // Read how many dirs need to be made
            int numDirs = Integer.parseInt(in.readLine());

            //Make each directory
            for(int i = 0; i < numDirs; i++){
                // Read the dir name on the server
                // NOTE: readLine chomps the line ending
                String dirName = in.readLine();
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
        } catch (IOException e) {
            System.err.println("IO Error when making directories");
            System.err.println(e);
        }
    }
}
