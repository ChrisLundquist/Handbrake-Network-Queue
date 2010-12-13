public class Command {
    public static final String GET_JOB = "Get_Job";
    public static final String CHECKOUT_JOB = "Checkout_Job";
    public static final String COMPLETE_JOB = "Complete_Job";
	public static final String NO_JOB = "No_Job";
    
    public static CommandType getCommandType(String command){
    	if(command.equals(GET_JOB))
    		return CommandType.GET_JOB;
    	else if(command.equals(CHECKOUT_JOB))
    		return CommandType.CHECKOUT_JOB;
    	else if(command.equals(COMPLETE_JOB))
    		return CommandType.COMPLETE_JOB;
    	return CommandType.UNKOWN;
    }
    
    public enum CommandType {
    	GET_JOB,CHECKOUT_JOB,COMPLETE_JOB,UNKOWN
    }
}
