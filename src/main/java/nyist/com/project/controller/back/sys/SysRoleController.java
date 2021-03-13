package nyist.com.project.controller.back.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nyist.com.project.constant.OperTypeCons;
import nyist.com.project.entity.SysMenuEntity;
import nyist.com.project.entity.SysRoleEntity;
import nyist.com.project.entity.SysRoleMenuEntity;
import nyist.com.project.log.SysLog;
import nyist.com.project.mapper.SysMenuMapper;
import nyist.com.project.mapper.SysRoleMapper;
import nyist.com.project.mapper.SysRoleMenuMapper;
import nyist.com.project.mapper.SysUserRoleMapper;
import nyist.com.project.result.Result;

/**
 * 角色管理
 * @author ljw
 *
 */
@Controller
@RequestMapping("/back/sys")
@Api(value = "", tags="后台角色接口")
public class SysRoleController {
	
	@Autowired
	SysRoleMapper sysRoleMapper;
	
	@Autowired
	SysRoleMenuMapper sysRoleMenuMapper;
	
	@Autowired
	SysUserRoleMapper sysUserRoleMapper;
	
	@Autowired
	SysMenuMapper sysMenuMapper;

	/**
	 * 角色列表跳转页面
	 * @return
	 */
	@ApiOperation(value = "后台角色", notes = "跳转到角色列表页面")
	@RequiresPermissions("sys:role:list")
	@GetMapping("/role/roleManage")
	public String toSysRole(){
		return "back/sys/roleManage";
	}
	
	/**
	 * 角色列表
	 * @return
	 */
	@ApiOperation(value = "后台角色", notes = "角色列表数据")
	@SysLog(operModule="角色管理",operType=OperTypeCons.VIEW,operDesc="查看角色列表")
	@RequiresPermissions("sys:role:list")
	@GetMapping("/role/getSysRole")
	@ResponseBody
	public Result getSysRole(){
		List<SysRoleEntity> roleList = sysRoleMapper.selectAll();
		return Result.ok(roleList);
	}

	/**
	 * 角色权限分配数据
	 * @return
	 */
	@ApiOperation(value = "后台角色", notes = "获取所有菜单以及当前角色拥有的菜单数据")
	@RequiresPermissions("sys:role:allote")
	@GetMapping("/role/getRoleMenu")
	@ResponseBody
	public Result getRoleMenu(@RequestParam String roleId){
		if(StringUtils.isEmpty(roleId)){
			return Result.fail("请刷新重试");
		}
		List<SysMenuEntity> list = sysMenuMapper.selectByLevel("1");//一级菜单
		list.forEach(menu->{
			List<SysMenuEntity> childs = sysMenuMapper.selectByPid(menu.getId());
			childs.forEach(child->{
				List<SysMenuEntity> childsTwo = sysMenuMapper.selectByPid(child.getId());//三级按钮
				child.setChilds(childsTwo);
			});
			menu.setChilds(childs);//二级按钮
		});
		Map<String,Object> map = new HashMap<>();
		map.put("menuList", list);
		List<SysRoleMenuEntity> callList = sysRoleMenuMapper.selectByRoleId(roleId);
		map.put("callList", callList);
		return Result.ok(map);
	}
	/**
	 * 角色分配权限操作
	 * @param roleIds
	 * @return
	 */
	
	@SysLog(operModule="角色管理",operType=OperTypeCons.MENU,operDesc="角色分配菜单权限")
	@RequiresPermissions("sys:role:allote")
	@PostMapping("/role/allotePerms")
	@ResponseBody
	public Result getSysRole1(@RequestParam String roleId,@RequestParam String menuIds){
		if(StringUtils.isEmpty(roleId)){
			return Result.fail("该角色可能不存在，请刷新重试");
		}
		if(StringUtils.isEmpty(menuIds)){
			sysRoleMenuMapper.deleteByRoleId(roleId);
			Result.ok();
		}else{
			//删除重建
			sysRoleMenuMapper.deleteByRoleId(roleId);
			String[]arr =  menuIds.split(",");
			for(String menuid:arr){
				sysRoleMenuMapper.insertByRoleIdAndMeunId(roleId,menuid);
			}
			return Result.ok();
		}
		return Result.fail();
	}

	
	/**
	 * 角色删除
	 * @param roleId
	 * @return
	 */
	@ApiOperation(value = "后台角色", notes = "删除对应角色")
	@SysLog(operModule="角色管理",operType=OperTypeCons.DEL,operDesc="角色删除")
	@RequiresPermissions("sys:role:del")
	@GetMapping("/role/roleDel")
	@ResponseBody
	public Result roleDel(@RequestParam String roleId){
		if(StringUtils.isEmpty(roleId)){
			return Result.fail("请刷新重试");
		}
		sysRoleMapper.deleteByPrimaryKey(roleId);//角色删除
		sysRoleMenuMapper.deleteByRoleId(roleId);//根据角色删除角色菜单表对应数据
		return Result.ok();
	}
	
	/**
	 * 添加角色
	 * @param name
	 * @return
	 */
	@ApiOperation(value = "后台角色", notes = "新建角色")
	@SysLog(operModule="角色管理",operType=OperTypeCons.ADD,operDesc="新建角色")
	@RequiresPermissions("sys:role:add")
	@GetMapping("/role/addRole")
	@ResponseBody
	public Result addRole(@RequestParam String name){
		if(StringUtils.isEmpty(name)){
			return Result.fail("角色名称不能为空");
		}
		SysRoleEntity selectOne = sysRoleMapper.selectByName(name);
		if(selectOne!=null){
			return Result.fail("该角色已经存在，不可重复添加");
		}
		SysRoleEntity entity = new SysRoleEntity();
		entity.setName(name);
		sysRoleMapper.insertSelective(entity);
		return Result.ok();
	}
	
}
