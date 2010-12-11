import java.net.*;
import java.io.*;

public class Client {
    public static int DEFAULT_PORT = 4444;
    String remote_host;
    int remote_port;
    Socket server = null;
    PrintWriter out = null;
    BufferedReader in = null;

    // If only Java had defailt parameters instead of making
    // reference chains
    Client(String remote_host){
        this(remote_host,DEFAULT_PORT);
    }

    Client(String remote_host, int remote_port) {
        this.remote_port = remote_port;
        this.remote_host = this.remote_host;
    }

    static public void main(String[] args){
        if( args.length < 1){
            System.err.println("Please specify a remote hostname");
            System.exit(-1);
        }
        if(args.length > 2) {
            System.err.println("Warning: Extra arguments passed, ignoring");
        }

        System.out.println("Connecting to " + args[0]);
        Client client = null;
        if(args.length == 2) {
            System.out.println("Using port " + args[1]);
            client = new Client(args[0],Integer.parseInt(args[1]));
        } else {
            client = new Client(args[0]);
        }
        client.start();
    }

    void start(){
        try {
            server = new Socket(remote_host, remote_port);
            out = new PrintWriter(server.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: taranis.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: taranis.");
            System.exit(1);
        }
        System.out.println("Connected to server");
    }

}
