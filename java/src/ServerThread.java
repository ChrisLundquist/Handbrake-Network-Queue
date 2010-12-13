import java.net.*;
import java.io.*;
class ServerThread extends Thread {
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;
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
            in = new BufferedReader( new InputStreamReader( client.getInputStream())); 
        } catch(IOException e){
            System.err.println("Failed to allocate resources to use Socket");
            System.err.println(e);
            System.exit(-2);
        }

        String inputLine;
        try {
            while ((inputLine = in.readLine()) != null){
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
            	out.println("ack");
            }
        } catch(IOException e){
            System.err.println("Exception while reading from socket");
            System.err.println(e);
            System.err.println("Continuing...");
        }
        clean();
    }

	private void clean() {
		try {
			in.close();
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
		}
	}
	
	private void checkoutJob() {
		int id = readID();
		System.out.println(client.getRemoteSocketAddress() + " has checked out job ID " + id);
		// The first thing we read is the ID of the job being checked out
	}
	
	private void completeJob() {
		int id = readID();
		System.out.println(client.getRemoteSocketAddress() + " has completed job ID " + id);
		// The first thing we read is the ID of the job being checked out
	}
	
	private int readID(){
		try {
			return Integer.parseInt(in.readLine());
		} catch (NumberFormatException e) {
			System.err.println("Unable to parse ID when reading from socket");
			System.err.println("Continuing...");
		} catch (IOException e) {
			System.err.println("IOException when reading job ID from socket");
			System.err.println("Continuing...");
		}
		return -1;
	}
}
