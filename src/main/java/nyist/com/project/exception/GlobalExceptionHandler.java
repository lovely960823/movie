package nyist.com.project.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nyist.com.project.result.Result;

/**
 * 定义全局运行异常
 * @author ljw 2020年11月2日14:36:07
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	
	private  final  static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler. class);
	/**
	 * shiro的无权限操作
	 * @return
	 */
	@ExceptionHandler(AuthorizationException.class)
	@ResponseBody
	public Result NoPerm(HttpServletRequest req){
		logger.info("请求[ "+req.getRequestURI()+" ]验证未通过:{}", "无权限操作");
		return Result.fail("无权限操作");
	}
	
	/**
	 * 请求或页面不存在异常
	 * @param e
	 * @param req
	 * @return
	 */
	@ExceptionHandler(value= UnavailableSecurityManagerException.class)
	public String UnavailableSecurityManagerException(UnavailableSecurityManagerException e,HttpServletRequest req){
		return "error/error";
	}
	
	/**
	 * 全局异常处理
	 * @param e
	 * @param req
	 * @return
	 */
	@ExceptionHandler(value= RuntimeException.class)
	@ResponseBody
	public Result runTimeException(RuntimeException e,HttpServletRequest req){
		logger.error("错误请求地址URI [{}]", req.getRequestURI());
		return Result.fail(e.getMessage());
	}
}
