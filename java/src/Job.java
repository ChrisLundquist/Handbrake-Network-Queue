import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.w3c.dom.NodeList;


public class Job {
    // Arbitrary numbers for status
    public static final String NEW = "New";
    public static final String CHECKED_OUT = "CheckedOut";
    public static final String COMPLETE = "Complete";
    public static final String CANCELED = "Canceled";

    // TODO: Use the tag names instead of the indexes
    // These tags must match those used in the XML queue file
    public static final String ID_TAG = "Id";
    public static final String QUERY_TAG = "Query";
    public static final String SOURCE_TAG = "Source";
    public static final String DESTINATION_TAG = "Destination";

    // The indexes in the document of the things that we care about
    public static final int ID_INDEX = 1;
    public static final int QUERY_INDEX = 3;
    public static final int SOURCE_INDEX = 7;
    public static final int DESTINATION_INDEX = 9;

    private int id;
    private int lastCheckedOut;
    private String status;
    private String query;
    private String source;
    private String destination;
    
    //
    public static void send(PrintWriter out, Job job ){
        out.println(job.id);
        out.println(job.query);
        out.println(job.source);
        out.println(job.destination);
    }
    
    public static Job recv(BufferedReader in){
        Job job = new Job();
        
        try {
            job.id = Integer.parseInt(in.readLine());
            job.query = in.readLine();
            job.source = in.readLine();
            job.destination = in.readLine();
        } catch (NumberFormatException e) {
            System.err.println("Unable to parse ID to int");
            System.exit(-2);
        } catch (IOException e) {
            System.err.println("Socket read error when receiving Job");
            System.exit(-3);
        }
        
        return job;
    }
    

    public Job(){
        lastCheckedOut = 0;
        status = NEW;
    }
    
    public Job(NodeList nodeList) throws NullPointerException {
        this(); // Set our defaults

        id = Integer.parseInt((nodeList.item(1).getTextContent()));
        query = nodeList.item(QUERY_INDEX).getTextContent();
        source = nodeList.item(SOURCE_INDEX).getTextContent();
        destination = nodeList.item(DESTINATION_INDEX).getTextContent();
    }

    public String toString(){
        return "ID: " + id + "\n" +
        "Query: " + query +"\n" +
        "Source:" + source + "\n" +
        "Destination:" + destination + "\n" +
        "Status: " + status + "\n" +
        "Last Checkout: " + (getLastCheckOut() > 0 ? getLastCheckOut() + " seconds" : "N/A"); 
    }

    // TODO error handling
    public boolean checkout(){
        // Don't checkout a job that we don't want to do
        // or has already been finished
        if(status == COMPLETE || status == CANCELED)
            return false;

        // NOTE: We might already be checked out but thats
        // alright, the first person to check out might suck.
        status = CHECKED_OUT;
        // So we know how long this job has been checked out
        lastCheckedOut = getTimeStamp();
        return true;
    }

    public boolean isNew(){
        return status == NEW;
    }

    public boolean isCheckedOut(){
        return status == CHECKED_OUT;
    }

    public boolean isComplete(){
        return status == COMPLETE;
    }

    public boolean isCanceled(){
        return status == CANCELED;
    }

    private int getTimeStamp(){
        return (int) (System.currentTimeMillis() / 1000);
    }

    private int getLastCheckOut(){
        if(isCheckedOut())
            return (int)(System.currentTimeMillis() / 1000) - lastCheckedOut;
        else
            return 0;
    }

    public int getId(){
        return id;
    }

    public String getQuery(){
        return query;
    }

    public void setQuery(String query){
        this.query = query;
    }

    public String getSource(){
        return source;
    }

    public void setSource(String source){
        this.source = source;
    }

    public String getDestination(){
        return destination;
    }

    public void setDestination(String destination){
        this.destination = destination;
    }
}
