
public class Job {
	private int id;
	private String query;
	private String source;
	private String destination;
	

	Job(){
		
	}
	
	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
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
