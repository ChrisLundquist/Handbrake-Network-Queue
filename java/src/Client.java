import java.net.*;
import java.io.*;

public class Client {
    public static int DEFAULT_PORT = 4444;
    String remote_host;
    int remote_port;
    Socket server = null;
    PrintWriter out = null;
    BufferedReader in = null;
    Job job = null;

    // If only Java had default parameters instead of making
    // reference chains
    Client(String remote_host){
        this(remote_host,DEFAULT_PORT);
    }

    Client(String remote_host, int remote_port) {
        this.remote_port = remote_port;
        this.remote_host = remote_host;
    }

    public void start(){
        while(getJob()){
            checkoutJob();
            doJob();
            completeJob();
        }
    }

    private void completeJob() {
        connect();
        out.println(Command.COMPLETE_JOB);
        // Send the id of our job
        out.println(job.getId());

        //TODO send the completed job back over
        disconnect();
    }

    private void doJob() {
        //TODO execute the job
    }

    private void checkoutJob() {
        connect();
        out.println(Command.CHECKOUT_JOB);
        // Send the id of our job
        out.println(job.getId());

        //TODO copy the files
        disconnect();
    }

    private boolean getJob(){
        connect();
        out.println(Command.GET_JOB);
        try {
            String jobsAvailable = in.readLine();
            if(jobsAvailable == null || jobsAvailable.equals(Command.NO_JOB)){
                System.out.println("Server has no jobs available");
                return false;
            } else {
                job = Job.recv(in);
                System.out.println("Got Job ID " + job.getId());
            }
        } catch (IOException e) {
            System.err.println("Error: IOError when retrieving job.");
        }
        disconnect();
        return true;
    }

    private void connect(){
        try {
            server = new Socket(remote_host, remote_port);
            out = new PrintWriter(server.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + remote_host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + remote_host);
            System.exit(1);
        }
        System.out.println("Connected to " + remote_host);
    }

    private void disconnect(){
        System.out.println("Closing connection to " + remote_host);
        try {
            server.close();
            out.close();
            in.close();
        } catch (IOException e) {
            System.err.println("Error when closing connection to " + remote_host);
        }
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
}
