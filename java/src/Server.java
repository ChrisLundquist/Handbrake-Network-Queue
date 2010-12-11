// Runs our server
import java.net.*; // ServerSocket, Socket
import java.io.*;  // PrintWriter BufferedReader InputStreamReader
public class Server {
    boolean listening;
    int port;
    ServerSocket server;

    Server(int port){
        listening = true;
        this.port = port;
    }

    void stop(){
        listening = false;
    }
    
    void start(){
        ServerSocket server = null;
        // Try to establish a server
        try {
            server = new ServerSocket(4444);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444");
            System.exit(-1);
        }

        // If we get a connection assign it to the client socket
        try {
            while(listening)
                (new ServerThread(server.accept())).run();
        } catch (IOException e) {
            System.err.println("Accept failed: 4444");
            System.exit(-1);
        }
    }

    public static void main(String[] args){
        Server server = new Server(4444);
        server.start();
    }
}
