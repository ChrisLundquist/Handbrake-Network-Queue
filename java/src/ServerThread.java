import java.net.*;
import java.io.*;
class ServerThread extends Thread {
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;
    ServerThread(Socket client){
        super("HandBrake Network Queue Server Thread");
        this.client = client;
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
			client.close();
			in.close();
			out.close();
		} catch (IOException e) {
			System.err.println("Error when closing resources from thread");
		}
	}

	private void getJob() {
		System.out.println("A Client has requested a job");		
	}
	
	private void checkoutJob() {
		System.out.println("A Client has checked out a job");
	}
	
	private void completeJob() {
		System.out.println("A Client has completed a job");
	}
}
