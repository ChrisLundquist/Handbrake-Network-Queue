// Runs our server
import java.net.*; // ServerSocket, Socket
import java.io.*;  // PrintWriter BufferedReader InputStreamReader
public class Server {
    boolean listening;
    int port;
    ServerSocket server;
    private String queueFile;
    public static JobQueue queue;
    public static int DEFAULT_PORT = 4444;

    Server(String queueFilePath){
        this(queueFilePath,DEFAULT_PORT);
    }

    Server(String queueFilePath, int port){
        listening = true;
        queueFile = queueFilePath;
        this.port = port;
        try{
        queue = new JobQueue(queueFile);
        } catch(IOException e){
            System.err.println("Could not open the queueFile specified: " + queueFile);
            System.err.println(e);
            System.exit(-2);
        }
    }

    void stop(){
        listening = false;
    }
    
    void start(){
        ServerSocket server = null;
        // Try to establish a server
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }

        // If we get a connection assign it to the client socket
        try {
            while(listening)
                (new ServerThread(server.accept())).run();
        } catch (IOException e) {
            System.err.println("Accept failed: " + port);
            System.exit(-1);
        }
    }

    private static void usage(){
        System.out.println(" remote_host_name_or_ip listening_port");
    }

    public static void main(String[] args){
        Server server = null;
        String filePath = null;
        int port;

        if(args.length == 0){
            System.err.println("No queue file specified");
            usage();
            System.exit(-1);
        }
        
        if(args.length == 1){
            filePath = args[0];
            server = new Server(filePath);
        }

        if(args.length == 2){
            port = Integer.parseInt(args[1]);
            server = new Server(filePath,port);
        }
        server.start();
    }
}
