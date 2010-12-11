// Manages Jobs into a queue
public class JobQueue {
    java.util.Vector<Job> queue;

    JobQueue() {
        //TODO
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
}
