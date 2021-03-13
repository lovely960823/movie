package nyist.com.project.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import nyist.com.project.entity.SysUserEntity;
import nyist.com.project.exception.AccountLocked;
import nyist.com.project.exception.InValidNameOrPwd;
import nyist.com.project.exception.MyNullPointerException;
import nyist.com.project.exception.UserNotExistsException;
import nyist.com.project.mapper.SysMenuMapper;
import nyist.com.project.mapper.SysUserMapper;

@Component
public class LoginService {
	
	@Autowired
	SysUserMapper userMapper;
	@Autowired
	SysMenuMapper sysMenuMapper;

	public SysUserEntity findUserByName(String username,String password){
		if(StringUtils.isEmpty(username)||StringUtils.isEmpty(password)){
			throw new MyNullPointerException("无效的用户名称");
		}
		SysUserEntity user = userMapper.findByUserName(username);
		if(user==null){
			 throw new UserNotExistsException("该用户不存在");
		}
		SysUserEntity user1  = userMapper.findByUserNameAndPwd(username,password);
		if(user1==null){
			 throw new InValidNameOrPwd("用户名或者密码不正确");
		}
		if(user1.getStatus().equals("1")){
			throw new AccountLocked("账号被冻结了");
		}
		return user1;
	};

}
