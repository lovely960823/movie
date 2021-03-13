package nyist.com.project.log;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nyist.com.project.entity.SysGlobalLogEntity;
import nyist.com.project.entity.SysUserEntity;
import nyist.com.project.mapper.SysGlobalLogMapper;

/**
 * 全局日志
 * @author ljw
 *
 */
@Aspect
@Component
public class GlobalLog {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	HttpServletRequest request;
	
	@Autowired
	SysGlobalLogMapper sysGlobalLogMapper;
	
	@Value("${logPath}")
	private String logPath;
	
	
	@Pointcut("execution(* nyist.com.project.controller..*.*(..))")
    public void aspect() {
    }
	
	@Before("aspect()")
	public void before(JoinPoint joinPoint) throws NoSuchMethodException, SecurityException{
		String content="uri:"+request.getRequestURI()+",type:"+request.getMethod()+"method:"+joinPoint.getSignature().toString()+",params:"+Arrays.toString(joinPoint.getArgs()).toString()+",ip:"+request.getRemoteAddr();
		
		SysGlobalLogEntity sysGlobalLog = new SysGlobalLogEntity();
		sysGlobalLog.setUri(request.getRequestURI());//等同于String methodName = joinPoint.getSignature().getName();
		sysGlobalLog.setType(request.getMethod());
		sysGlobalLog.setMethod(joinPoint.getSignature().toString());
		sysGlobalLog.setIp(request.getRemoteAddr());
		sysGlobalLog.setParams(Arrays.toString(joinPoint.getArgs()).toString());
		sysGlobalLog.setCreateTime(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));
		//sysGlobalLog.setResults(results);
		
		Subject currentSubject = SecurityUtils.getSubject();
		Session session = currentSubject.getSession();
		SysUserEntity loginUser = (SysUserEntity)session.getAttribute("loginUser");
		if(loginUser!=null){
			sysGlobalLog.setUserId(loginUser.getId());
			sysGlobalLog.setUserName(loginUser.getAccount());
			content=content+",操作人id："+loginUser.getId()+",操作人员："+loginUser.getAccount();
		}
		sysGlobalLogMapper.insertSelective(sysGlobalLog);
		
		String fileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		fileName=logPath+fileName+".txt";
		File f= new File(fileName);
		if(!f.exists()){
			f.getParentFile().mkdirs();     
		}
		Writer out = null ; 
		try {
			out = new FileWriter(f,true) ; 
			out.write("["+new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date())+":"+content+"]");
			out.write("\r\n");
			out.close() ;
		} catch (Exception e) {
		}
	}

	@AfterReturning(returning="obj",pointcut="aspect()")
	public void methodAfterReturning(Object obj) throws JsonProcessingException{
		//返回结果
		//System.err.println("**********"+objectMapper.writeValueAsString(obj));
	}
    
}
