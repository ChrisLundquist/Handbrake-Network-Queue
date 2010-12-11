import java.net.*;
import java.io.*;
class ServerThread extends Thread {
    private Socket client;
    ServerThread(Socket client){
        super("HandBrake Network Queue Server Thread");
        this.client = client;
    }

    public void run(){
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
