package nyist.com.project.shiro;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import nyist.com.project.constant.RedisKeyCons;
import nyist.com.project.entity.SysMenuEntity;
import nyist.com.project.entity.SysUserEntity;
import nyist.com.project.mapper.SysMenuMapper;
import nyist.com.project.mapper.SysUserMapper;
import nyist.com.project.service.LoginService;
import nyist.com.project.utils.MD5Util;
import nyist.com.project.utils.RedisUtil;


public class MyRealm extends AuthorizingRealm{
	

	@Autowired
	private LoginService loginService;
	
	@Autowired
	private SysMenuMapper sysMenuMapper;
	
	@Autowired
	CredentialsMatcher credentialsMatcher;
	
	@Autowired
	RedisUtil redisUtil;
	//授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		//User principal = (User)SecurityUtils.getSubject().getPrincipal();//获取SimpleAuthenticationInfo 的第一个参数
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
	    SysUserEntity user = (SysUserEntity)session.getAttribute("loginUser");
	    List<SysMenuEntity> list = sysMenuMapper.findAllPer(user.getId());
	    list.forEach(menu->{
	    	if(!StringUtils.isEmpty(menu.getPerm())){
	    		info.addStringPermission(menu.getPerm());
	    	}
	    });
	    if(user.getAccount().equals("ljw")){
	    	info.addStringPermission("*:*");
	    	info.addRole("ljw");
	    }
		return info;
	}

	//认证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		//String inputName = (String) token.getPrincipal();//可以获取当前用户名称
		/**
	       private String username;
		    private char[] password;
	     */
		UsernamePasswordToken userToken = (UsernamePasswordToken)token;
		String username = userToken.getUsername();
		//String password = userToken.getPassword().toString();
		String password = new String(userToken.getPassword());
		SysUserEntity user = null;
		try {
			user = loginService.findUserByName(username,MD5Util.md5Encrypt32Lower(password+"LJW"));
		} catch (Exception e) {
			throw new AuthenticationException(e.getMessage(), e);
		}
		//将登陆用户存入session中
		Subject currentSubject = SecurityUtils.getSubject();
		Session session = currentSubject.getSession();
	    session.setAttribute("loginUser",user);
		return new SimpleAuthenticationInfo(user, user.getPassword(), "");
	}
	
	
	@PostConstruct
    public void myCredentialsMatcher() {
        //该句作用是重写shiro的密码验证，让shiro用我自己的验证
        setCredentialsMatcher(credentialsMatcher);
    }

}
