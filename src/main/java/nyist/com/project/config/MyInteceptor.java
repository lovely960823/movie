package nyist.com.project.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerInterceptor;

import nyist.com.project.entity.SysUserEntity;

public class MyInteceptor implements HandlerInterceptor{

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception {
		String uri=request.getRequestURI();
		Subject currentSubject = SecurityUtils.getSubject();
		Session session = currentSubject.getSession();
	    SysUserEntity user = (SysUserEntity)session.getAttribute("loginUser");
	    if(user==null){
	    	if(uri.toLowerCase().indexOf("login")>-1){
	    		return true;
	    	}else{
	    		response.setCharacterEncoding("UTF-8");
	    		response.getWriter().print("<html><head><meta charset='UTF-8'>" +
	    				"<link rel='stylesheet' href='https://www.layuicdn.com/layui-v2.5.4/css/layui.css'>" +
	    				"<script src='https://www.layuicdn.com/layui-v2.5.4/layui.js' charset='utf-8'></script>" +
	    				"</head><script>" +
	    				"layui.use('layer', function(){" +
	    				"var $ = layui.jquery, layer = layui.layer;"+
	    				"layer.alert('身份过期，请重新登录！！', { icon: 0, closeBtn: 0 }, function (index) { "+
	    				"   window.location.href='/toLogin'    "+
	    				"});" +
	    				"});"+
	    				"</script>");
	    		return false;
	    	}
	    }
        return true;
	}
}
