// Manages Jobs into a queue
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JobQueue {
    public static String JOB_TAG = "Job";
    private java.util.Vector<Job> queue;
    private Document queueDoc;

    JobQueue(String filePath) throws IOException{
        queue = new java.util.Vector<Job>();
        queueDoc = parseFile(filePath);
        makeJobs();
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
    private void makeJobs(){
        NodeList jobs = queueDoc.getElementsByTagName(JOB_TAG);
        // Make a job for each entry in the queue document
        for(int i = 0; i < jobs.getLength();i++){
            try{
                queue.add(new Job(jobs.item(i).getChildNodes()));
            } catch( Exception e){
                System.err.println("Error when attempting to instantiate job from XML.");
                System.err.println(e);
                System.err.println("Continuing..\n");
            }
        }
        // Make sure there is work to do now Scooby
        if(0 == queue.size()){
            System.err.println("No valid jobs found in queue file...exiting");
            System.exit(-14);
        }

        System.out.println("Found " + queue.size() + " jobs" );
    }

    private Document parseFile(String filePath){
        System.out.println("Parsing Queue file XML ... " + filePath);
        DocumentBuilder docBuilder;
        Document doc = null;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            System.err.println("Wrong parser configuration: " + e.getMessage());
            return null;
        }
        try {
            doc = docBuilder.parse(new File(filePath));
        }
        catch (SAXException e) {
            System.err.println("Wrong XML file structure: " + e.getMessage());
            return null;
        }
        catch (IOException e) {
            System.err.println("Could not read source file: " + e.getMessage());
            System.exit(-7);
        }
        System.out.println("Queue file parsed");
        return doc;
    }

    public Job checkout(int id) {
        for(Job job : queue){
            if(job.getId() == id){
                job.checkout();
                return job;
            }
        }
        return null;
    }

    public Job complete(int id) {
        for(Job job : queue){
            if(job.getId() == id){
                job.complete();
                return job;
            }
        }
        return null;
    }
}
