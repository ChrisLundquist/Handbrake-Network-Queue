import java.net.*;
import java.io.*;

public class Client {
	public static int DEFAULT_PORT = 4444;
	String remote_host;
	int remote_port;
	Socket server = null;
	PrintWriter out = null;
	BufferedReader in = null;

	// If only Java had default parameters instead of making
	// reference chains
	Client(String remote_host){
		this(remote_host,DEFAULT_PORT);
	}

	Client(String remote_host, int remote_port) {
		this.remote_port = remote_port;
		this.remote_host = remote_host;
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
		connectToServer();
		do{
		getJob();
		getFilesForJob();
		doJob();
		completeJob();
		}while(getJob());
		closeConnectionToServer();
	}

	private void completeJob() {
		//connectToServer();
		out.println(Command.COMPLETE_JOB);
		//closeConnectionToServer();
		//TODO send the completed job back over
	}

	private void doJob() {
		//TODO execute the job
	}

	private void getFilesForJob() {
		//connectToServer();
		out.println(Command.CHECKOUT_JOB);
		//closeConnectionToServer();
		//TODO copy the files
	}

	private boolean getJob(){
		//connectToServer();
		out.println(Command.GET_JOB);
		try {
			in.readLine();
			//TODO get the job Object
		} catch (IOException e) {
			System.err.println("Error: IOError when retrieving job.");
		}
		//closeConnectionToServer();
		return false;
	}
	
	void connectToServer(){
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
		System.out.println("Connected to server");
	}
	
	void closeConnectionToServer(){
		System.out.println("Closing connection to server");
		try {
			server.close();
			out.close();
			in.close();
		} catch (IOException e) {
			System.err.println("Error when closing connection to server.");
		}
	}

}
