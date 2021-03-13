package nyist.com.project.exception;

public class AccountLocked extends RuntimeException{

	private static final long serialVersionUID = 1L;

    public AccountLocked() {
        super();
    }

    public AccountLocked(String s) {
        super(s);
    }
    public AccountLocked(Throwable cause) {
        super(cause);
    }
    public AccountLocked(String message, Throwable cause,boolean enableSuppression,boolean writableStackTrace) {
    	super(message, cause, enableSuppression, writableStackTrace);
	}
}
