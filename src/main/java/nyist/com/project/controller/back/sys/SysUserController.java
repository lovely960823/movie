package nyist.com.project.controller.back.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nyist.com.project.constant.OperTypeCons;
import nyist.com.project.entity.SysRoleEntity;
import nyist.com.project.entity.SysUserRoleEntity;
import nyist.com.project.entity.SysUserEntity;
import nyist.com.project.log.SysLog;
import nyist.com.project.mapper.SysRoleMapper;
import nyist.com.project.mapper.SysUserRoleMapper;
import nyist.com.project.mapper.SysUserMapper;
import nyist.com.project.result.Result;
import nyist.com.project.utils.MD5Util;
/**
 * 用户管理
 * @author ljw
 *
 */
@RestController
@RequestMapping("/back/sys")
@Api(value = "", tags="后台用户接口")
public class SysUserController {

	@Autowired
	SysUserMapper userMapper;
	
	@Autowired
	SysUserRoleMapper sysUserRoleMapper;
	
	@Autowired
	SysRoleMapper sysRoleMapper;

	@ApiOperation(value = "后台用户", notes = "跳转到用户列表页面")
	@RequiresPermissions("sys:user:list")
	@GetMapping("/user/userManage")
	public ModelAndView toSysUser(){
		return new ModelAndView("back/sys/userManage");
	}
	
	/**
	 * 系统用户列表
	 * @return
	 */
	@ApiOperation(value = "后台用户", notes = "获取所有用户数据列表")
	@SysLog(operModule="用户管理",operType=OperTypeCons.VIEW,operDesc="查看用户列表")
	@RequiresPermissions("sys:user:list")
	@GetMapping("/user/getSysUser")
	public Result getSysUser(){
		List<SysUserEntity> userList = userMapper.selectAllUser();
		return Result.ok(userList);
	}
	
	/**
	 * 获取所有角色列表和当前用户有的角色列表数据
	 * @param userId
	 * @return
	 */
	@ApiOperation(value = "后台用户", notes = "获取所有角色列表和当前用户有的角色列表数据")
	@RequiresPermissions("sys:user:allote")
	@GetMapping("/user/roleList")
	public Result roleList(@RequestParam Integer userId){
		List<SysRoleEntity> roleList = sysRoleMapper.selectAll();
		List<SysUserRoleEntity> callList = sysUserRoleMapper.selectByUserId(userId);
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("roleList", roleList);
		map.put("callList", callList);
		return Result.ok(map);
	}
	
	/**
	 * 用户分配角色确定操作
	 * @param userId
	 * @return
	 */
	@ApiOperation(value = "后台用户", notes = "用户分配角色确定操作")
	@SysLog(operModule="用户管理",operType=OperTypeCons.ROLE,operDesc="用户分配角色")
	@RequiresPermissions("sys:user:allote")
	@GetMapping("/user/roleAllocate")
	public Result roleAllocate(@RequestParam Integer userId,@RequestParam String roleIds){
		if(StringUtils.isEmpty(userId)){
			return Result.fail("请刷新重试");
		}
		if(StringUtils.isEmpty(roleIds)){
			sysUserRoleMapper.deleteByUserId(userId);
			return Result.ok();
		}else{
			sysUserRoleMapper.deleteByUserId(userId);
			String[] arr = roleIds.split(",");
			for(String roleId:arr){
				sysUserRoleMapper.insertByUserIdAndRoleId(userId,roleId);
			}
			return Result.ok();
		}
	}
	
	/**
	 * 重置用户密码
	 * @param id
	 * @return
	 */
	@RequiresPermissions("sys:user:edit")
	@GetMapping("/user/rePWd/{id}")
	public Result rePwd(@PathVariable("id")Integer id) {
		String string = MD5Util.md5Encrypt32Lower("123456LJW");
		SysUserEntity primaryKey = userMapper.selectByPrimaryKey(id);
		if(primaryKey==null) {
			return Result.fail("当前用户不存在，请刷新重试");
		}
		primaryKey.setPassword(string);
		userMapper.updateByPrimaryKeySelective(primaryKey);
		return Result.ok("修改成功");
	}
	
	/**
	 * 根据用户id删除
	 * @param id
	 * @return
	 */
	@RequiresPermissions("sys:user:del")
	@GetMapping("/user/delById/{id}")
	public Result delById(@PathVariable("id")Integer id) {
		SysUserEntity primaryKey = userMapper.selectByPrimaryKey(id);
		if(primaryKey==null) {
			return Result.fail("当前用户不存在，请刷新重试");
		}
		userMapper.deleteByPrimaryKey(id);
		return Result.ok("修改成功");
	}
	
}
