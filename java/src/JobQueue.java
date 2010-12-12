// Manages Jobs into a queue
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.*;

public class JobQueue {
    private java.util.Vector<Job> queue;
    private XMLDecoder in;
    private XMLEncoder out;

    JobQueue(String filePath) throws IOException{
        parseFile(filePath);
    }

    public Job getNextJob(){
        for(int i = 0; i < queue.size(); i++){
            Job job = queue.get(i);
            // Skip the jobs that are complete or canceled
            if(job.isComplete() || job.isCanceled())
            continue;
        // If we have a new Job return the first new job we find
        if(job.isNew())
            return job;
        // TODO Return a checked out job
        }
        // We couldn't find a job
        return null;
    }

    private Document parseFile(String filePath){
        in = new XMLDecoder(new BufferedInputStream(new FileInputStream(filePath)));
        System.out.println(in);

    }
}
