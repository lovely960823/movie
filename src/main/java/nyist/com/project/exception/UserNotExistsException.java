package nyist.com.project.exception;

/**
 * 仿照空指针异常源码写的   用户不存在异常
 * @author ljw
 *
 */
public class UserNotExistsException extends RuntimeException {

	 private static final long serialVersionUID = 1L;

	    public UserNotExistsException() {
	        super();
	    }

	    public UserNotExistsException(String s) {
	        super(s);
	    }
}
