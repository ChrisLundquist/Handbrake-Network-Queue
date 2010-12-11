// Runs our server
import java.net.*; // ServerSocket, Socket
import java.io.*;  // PrintWriter BufferedReader InputStreamReader
public class Server {
    public static void main(String[] args){
        System.out.println("Hello World");
        run();
    }

    static void run(){
        ServerSocket server = null;
        // Try to establish a server
        try {
            server = new ServerSocket(4444);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444");
            System.exit(-1);
        }

        // If we get a connection assign it to the client socket
        Socket client = null;
        try {
            // Blocking accept
            client = server.accept();
        } catch (IOException e) {
            System.err.println("Accept failed: 4444");
            System.exit(-1);
        }

        // Have to declare them here for scope
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            // Java stupidity declaring objects for objects
            out = new PrintWriter( client.getOutputStream(), true);
            in = new BufferedReader( new InputStreamReader( client.getInputStream())); 
        } catch(IOException e){
            System.err.println("Failed to allocate resources to use Socket");
            System.err.println(e);
            System.exit(-2);
        }

        String inputLine, outputLine;
        try {
            while ((inputLine = in.readLine()) != null){
                System.out.println(inputLine);
                out.println("ack");
            }
        } catch(IOException e){
            System.err.println("Exception while reading from socket");
            System.err.println(e);
            System.exit(-3);
        }
    }
}
