import java.io.IOException;

class JobQueueTest {
    private static final String TEST_FILE = "test.queue";

    static public void main(String[] args){
    	JobQueue queue = null;
        try {
			queue = new JobQueue(TEST_FILE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println(queue.getNextJob());
    }
}
