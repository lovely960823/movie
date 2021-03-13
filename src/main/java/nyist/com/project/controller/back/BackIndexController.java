package nyist.com.project.controller.back;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nyist.com.project.constant.RedisKeyCons;
import nyist.com.project.entity.SysMenuEntity;
import nyist.com.project.entity.SysUserEntity;
import nyist.com.project.mapper.SysMenuMapper;
import nyist.com.project.utils.RedisUtil;

@Controller
@Api(value = "", tags="后台首页接口")
public class BackIndexController {

	@Autowired
	private SysMenuMapper sysMenuMapper;
	
	@Autowired
	RedisUtil redisUtil;
	/**
	 * 后台首页菜单模板
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "后台首页加载", notes = "加载当前用户权限菜单")
	@GetMapping("/back/index")
	public String index(HttpServletRequest request){
		List<SysMenuEntity> list = new ArrayList<SysMenuEntity>();
		Subject currentSubject = SecurityUtils.getSubject();
		Session session = currentSubject.getSession();
		SysUserEntity loginUser = (SysUserEntity)session.getAttribute("loginUser");
		list = sysMenuMapper.findAllPer(loginUser.getId());
		if(loginUser.getAccount().equals("ljw")){
			list = sysMenuMapper.selectAll();
		}
		request.setAttribute("menuList", list);
		return "back/index";
	}
	
	@ApiOperation(value = "后台首页加载", notes = "附带页面")
	@GetMapping("/back/index_v1")
	public String index_v1(){
		return "back/index_v1";
	}

	/**
	 * 用户退出操作
	 * @return
	 */
	@ApiOperation(value = "用户退出登录",notes = "清除session")
	@GetMapping("/logout")
	public String logout(){
		Subject currentSubject = SecurityUtils.getSubject();
		currentSubject.logout();
		return "login";
	}
}
