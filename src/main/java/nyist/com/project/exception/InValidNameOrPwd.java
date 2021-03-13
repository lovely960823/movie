package nyist.com.project.exception;

/**
 * 用户名账号或者密码不正确异常
 * @author Administrator
 *
 */
public class InValidNameOrPwd extends RuntimeException {

	 private static final long serialVersionUID = 1L;

	    public InValidNameOrPwd() {
	        super();
	    }

	    public InValidNameOrPwd(String s) {
	        super(s);
	    }
}
