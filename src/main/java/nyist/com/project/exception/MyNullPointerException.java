package nyist.com.project.exception;

public class MyNullPointerException extends RuntimeException{

	private static final long serialVersionUID = 1L;

    public MyNullPointerException() {
        super();
    }

    public MyNullPointerException(String s) {
        super(s);
    }
}
