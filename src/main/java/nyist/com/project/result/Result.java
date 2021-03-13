package nyist.com.project.result;

import java.io.Serializable;
import lombok.Data;

/**
 * 封装一个统一返回结果类
 * @author Administrator
 *
 */

@Data
public class Result implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int code=200; 
    private String msg="操作成功";
    private Object data;

    /**
     * 响应成功
     * @return
     */
    public static Result ok() {
		return new Result();
	}

    public static Result ok(String msg) {
    	Result result = new Result();
    	result.setMsg(msg);
		return result;
	}
    
    public static Result ok(Object data) {
    	Result result = new Result();
    	result.setData(data);
		return result;
	}
    public static Result ok(Object data,String msg) {
    	Result result = new Result();
    	result.setData(data);
    	result.setMsg(msg);
		return result;
	}
    
    /**
     * 操作失败的
     * @return
     */
    public static Result fail() {
    	Result result = new Result();
    	result.setCode(400);
    	result.setMsg("操作失败");
		return result;
	}
    
    public static Result fail(String msg) {
    	Result result = new Result();
    	result.setCode(400);
    	result.setMsg(msg);
		return result;
	}
    public static Result fail(Object data) {
    	Result result = new Result();
    	result.setCode(400);
    	result.setMsg("操作失败");
    	result.setData(data);
		return result;
	}
    public static Result fail(Object data,String msg) {
    	Result result = new Result();
    	result.setCode(400);
    	result.setMsg(msg);
    	result.setData(data);
		return result;
	}
    
    public static Result fail(String msg,int code) {
    	Result result = new Result();
    	result.setCode(code);
    	result.setMsg(msg);
		return result;
	}
}
