package nyist.com.project.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.springframework.stereotype.Component;

import nyist.com.project.utils.MD5Util;

@Component
public class CredentialsMatcher extends SimpleCredentialsMatcher{

	 @Override
	    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
	        UsernamePasswordToken authcToken = (UsernamePasswordToken) token;
	        Object tokenCredentials = MD5Util.md5Encrypt32Lower(String.valueOf(authcToken.getPassword())+"LJW");
	        Object accountCredentials = getCredentials(info);
	        return accountCredentials.equals(tokenCredentials);
	    }
}
