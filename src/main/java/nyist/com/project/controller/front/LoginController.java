package nyist.com.project.controller.front;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nyist.com.project.constant.OperTypeCons;
import nyist.com.project.entity.SysUserEntity;
import nyist.com.project.log.SysLog;
import nyist.com.project.mapper.SysMenuMapper;
import nyist.com.project.mapper.SysUserMapper;
import nyist.com.project.result.Result;
import nyist.com.project.utils.MD5Util;

@Controller
@Api(value="", tags="后台登录接口")
public class LoginController {

	private final static String SALT="LJW";
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	SysUserMapper userMapper;
	
	@Autowired
	SysMenuMapper sysMenuMapper;
	
	
	@ApiOperation(value="登录页面跳转",notes="用户登录")
	@GetMapping("/toLogin")
	public String toLogin(HttpServletRequest request){
		request.setAttribute("ip", "ip:"+request.getRemoteHost()+" port:"+request.getLocalPort());
		return "front/login";
	}
	
	/**
	 * 用户账号密码登录
	 * @param user
	 * @return
	 */
	@ApiOperation(value="登录请求",notes="用户登录")
	@SysLog(operModule="用户登陆",operType=OperTypeCons.LOGIN,operDesc="用户登录")
	@GetMapping("/login")
	@ResponseBody
	public Result myLogin(SysUserEntity user){
		Subject subject = SecurityUtils.getSubject();
		//封装用户登录数据，处理用户密码
		UsernamePasswordToken token = new UsernamePasswordToken(user.getAccount(), user.getPassword());
		try {
			subject.login(token);
			return Result.ok();
		} catch (Exception e) {
			return Result.fail(e.getMessage());
		}
	}
	
	/**
	 * 微信授权请求页面
	 * @param response
	 */
	@GetMapping("/wxLogin")
	public void test(HttpServletResponse response) {
		String baseUrl="https://open.weixin.qq.com/connect/qrconnect?"
				+ "appid=wx3bdb1192c22883f3"
				+"&redirect_uri=http://note.java.itcast.cn/weixinlogin"
				+"&response_type=code&scope=snsapi_login&state=STATE#wechat_redirect";
		try {
			response.sendRedirect(baseUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 回调页面
	 * @param code
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/weixinlogin")
	public String wxcallback(String code,HttpServletResponse response) throws Exception {
		String baseUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?code=" + code + "&appid=wx3bdb1192c22883f3&secret=db9d6b88821df403e5ff11742e799105&grant_type=authorization_code";
		String msg = restTemplate.getForObject(baseUrl, String.class);
		Map<String, Object> map1 = JSON.parseObject(msg, Map.class);//字符串形式转map
        Object access_token = map1.get("access_token");
        Object openid = map1.get("openid");
        String infoUrl="https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid;
        //获取到的用户信息也是String格式，也需要转一下map，和上面那个类似
        String user_info =restTemplate.getForObject(infoUrl.toString(), String.class);
        JSONObject parseObject = JSON.parseObject(user_info);
        Map<String, Object> map= new HashMap<String, Object>();
        map=parseObject;
        SysUserEntity user = userMapper.findByOpenid(openid);
        if(user==null){
        	SysUserEntity newUser = new SysUserEntity();
        	newUser.setAccount(openid.toString());
        	newUser.setPassword("wxlogin"+SALT);
        	newUser.setRealName(map.get("nickname").toString());
        	newUser.setStatus("0");
        	newUser.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        	newUser.setOpenid(openid.toString());
        	newUser.setProfile(map.get("headimgurl").toString());
        	newUser.setLastLoginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        	userMapper.insertSelective(newUser);
        }
        user.setAccount(openid.toString());
        user.setRealName(map.get("nickname").toString());
        user.setLastLoginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        user.setProfile(map.get("headimgurl").toString());
        userMapper.updateByPrimaryKeySelective(user);
        Subject subject = SecurityUtils.getSubject();
		//封装用户登录数据，处理用户密码
		UsernamePasswordToken token = new UsernamePasswordToken(user.getAccount(), MD5Util.md5Encrypt32Lower("wxlogin"));
		try {
			subject.login(token);
			return "redirect:/back/index";
		} catch (Exception e) {
			return "redirect:front/login";
		}
        
	}
}
