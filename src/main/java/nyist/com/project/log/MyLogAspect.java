package nyist.com.project.log;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.alibaba.fastjson.JSONObject;

import nyist.com.project.entity.ExceptionLogEntity;
import nyist.com.project.entity.SysLogEntity;
import nyist.com.project.entity.SysUserEntity;
import nyist.com.project.mapper.ExceptionLogMapper;
import nyist.com.project.mapper.SysLogMapper;
/**
 * 自定义注解日志和异常日志
 * @author ljw
 *
 */

@Aspect
@Component
public class MyLogAspect {

	@Autowired
    private SysLogMapper sysLogMapper;
	@Autowired
	HttpServletRequest request;

	/**
	 * 普通日志记录
	 */
	
    //定义切点 @Pointcut
    //在注解的位置切入代码
    @Pointcut("@annotation(nyist.com.project.log.SysLog)")
    public void logPoinCut() {
    }

    /**
     * 	切面 配置通知
     *  根据需要在切入点不同位置的切入内容
	 *	使用@Before在切入点开始处切入内容
	 *	使用@After在切入点结尾处切入内容
	 *	使用@AfterReturning在切入点return内容之后切入内容（可以用来对处理返回值做一些加工处理）
	 *	使用@Around在切入点前后切入内容，并自己控制何时执行切入点自身的内容
	 *	使用@AfterThrowing用来处理当切入内容部分抛出异常之后的处理逻辑
     */
    //@AfterReturning("logPoinCut()") //如果出现异常，则捕获不到
    @Before("logPoinCut()")
    public void saveSysLog(JoinPoint joinPoint) {
        //保存日志
        SysLogEntity sysLog = new SysLogEntity();
        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();
        //获取操作
        SysLog myLog = method.getAnnotation(SysLog.class);
        sysLog.setOperDesc(myLog.operDesc());
        sysLog.setOperModule(myLog.operModule());
        sysLog.setOperType(myLog.operType());
        //获取请求的类名
        String className = joinPoint.getTarget().getClass().getName();
        //记录一下哪个类下面的哪个请求
        sysLog.setMethod(className+":"+request.getRequestURI());
        //记录请求参数
        Enumeration<String> paramter = request.getParameterNames();
        JSONObject json = new JSONObject();
		while (paramter.hasMoreElements()) {
			String str = (String) paramter.nextElement();
			json.put(str, request.getParameter(str));
		}
		sysLog.setParams(json.toJSONString());
        sysLog.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        Subject currentSubject = SecurityUtils.getSubject();
		Session session = currentSubject.getSession();
		SysUserEntity loginUser = (SysUserEntity)session.getAttribute("loginUser");
		if(loginUser!=null){
			//获取用户名
			sysLog.setUsername(loginUser.getAccount());
			//sysLog.setId(loginUser.getId());
		}
        //获取用户ip地址
        sysLog.setIp(request.getRemoteAddr());
        //保存
        sysLogMapper.insertSelective(sysLog);
    }
    
    
    
    
    
    
    /**
     * 异常记录
     */
    
    @Pointcut("execution(* nyist.com.project..*.*(..))")
    public void operExceptionLogPoinCut() {
    }

    @Autowired
    private ExceptionLogMapper exceptionLogMapper;
    
    @AfterThrowing(pointcut = "operExceptionLogPoinCut()", throwing = "e")
    public void saveExceptionLog(JoinPoint joinPoint, Throwable e) {
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        ExceptionLogEntity excepLog = new ExceptionLogEntity();
        try {
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = className+":"+request.getRequestURI();
            // 请求的参数
            Enumeration<String> paramter = request.getParameterNames();
            JSONObject json = new JSONObject();
    		while (paramter.hasMoreElements()) {
    			String str = (String) paramter.nextElement();
    			json.put(str, request.getParameter(str));
    		}
            excepLog.setParams(json.toJSONString()); // 请求参数
            excepLog.setMethod(methodName); // 请求方法名
            excepLog.setName(e.getClass().getName()); // 异常名称
            //excepLog.setExcMessage(stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace())); // 异常信息
            excepLog.setMessage(e.getMessage());
            Subject currentSubject = SecurityUtils.getSubject();
    		Session session = currentSubject.getSession();
    		SysUserEntity loginUser = (SysUserEntity)session.getAttribute("loginUser");
            //获取用户名
    		if(loginUser!=null){
    			excepLog.setUserId(loginUser.getId()+""); // 操作员ID
    			excepLog.setUserName(loginUser.getAccount()); // 操作员名称
    		}
            excepLog.setIp(request.getRemoteAddr()); // 操作员IP
            excepLog.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // 发生异常时间
            exceptionLogMapper.insertSelective(excepLog);
        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }

    public String stackTraceToString(String exceptionName, String exceptionMessage, StackTraceElement[] elements) {
        StringBuffer strbuff = new StringBuffer();
        for (StackTraceElement stet : elements) {
            strbuff.append(stet + "\n");
        }
        String message = exceptionName + ":" + exceptionMessage + "\n\t" + strbuff.toString();
        return message;
    }
}
