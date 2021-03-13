package nyist.com.project.exception;

public class NoRepeatException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public NoRepeatException(String msg) {
	        super(msg);
	    }

	    public NoRepeatException(String msg, Throwable cause) {
	        super(msg, cause);
	    }
}
