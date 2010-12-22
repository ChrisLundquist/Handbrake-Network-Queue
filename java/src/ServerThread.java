import java.net.*;
import java.io.*;
class ServerThread extends Thread {
    private Socket client;
    private PrintWriter out;
    private JobQueue queue;

    ServerThread(Socket client, JobQueue queue){
        super("HandBrake Network Queue Server Thread");
        this.client = client;
        this.queue = queue;
    }

    public void run(){
        try {
            // Java stupidity declaring objects for objects
            out = new PrintWriter( client.getOutputStream(), true);
        } catch(IOException e){
            System.err.println("Failed to allocate resources to use Socket");
            System.err.println(e);
            System.exit(-2);
        }

        String inputLine;
        while ((inputLine = FileTransfer.readLine(client)) != null){
            switch(Command.getCommandType(inputLine)){
            case GET_JOB:
                getJob();
                break;
            case CHECKOUT_JOB:
                checkoutJob();
                break;
            case COMPLETE_JOB:
                completeJob();
                break;
            default:
                System.err.println("Error: Unkown Command: " + inputLine);
                System.err.println("Continuing...");
            }
        }
        clean();
    }

    private void clean() {
        try {
            out.close();
            client.close();
        } catch (IOException e) {
            System.err.println("Error when closing resources from thread");
        }
    }

    private void getJob() {
        System.out.println( client.getRemoteSocketAddress() + " has requested a job");		
        Job job = queue.getNextJob();
        if(job == null){
            // Tell them that we have no job
            out.println(Command.NO_JOB);
        }else{
            out.println(Command.HAVE_JOB);
        }
        try {
            Job.send(new PrintWriter(client.getOutputStream()), job);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkoutJob() {
        // The first thing we read is the ID of the job being checked out
        int id = readID();
        System.out.println(client.getRemoteSocketAddress() + " has checked out job ID " + id);
        
        Job job = queue.checkout(id);
        FileTransfer.sendDirs(client, job.relativeDirsWithSourceDir());
        
        FileTransfer.send(client, job.relativeFilesWithSourceDir());
    }

    private void completeJob() {
        // The first thing we read is the ID of the job being checked out
        int id = readID();
        FileTransfer.recv(client);
        queue.complete(id);
        System.out.println(client.getRemoteSocketAddress() + " has completed job ID " + id);
    }

    private int readID(){
        try {
            return Integer.parseInt(FileTransfer.readLine(client));
        } catch (NumberFormatException e) {
            System.err.println("Unable to parse ID when reading from socket");
            System.err.println("Continuing...");
        }
        return -1;
    }
}
