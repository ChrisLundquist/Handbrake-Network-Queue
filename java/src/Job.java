
public class Job {
    public static final int NEW = 0x42;
    public static final int CHECKED_OUT = 0x43;
    public static final int COMPLETE = 0x44;
    public static final int CANCELED = 0x45;

    private int id;
    private int status;
    private int lastCheckedOut;
    private String query;
    private String source;
    private String destination;

    Job(){
        // TODO
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
        // TODO return Time.now
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
