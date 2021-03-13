package nyist.com.project.controller.back.sys;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nyist.com.project.constant.OperTypeCons;
import nyist.com.project.entity.SysMenuEntity;
import nyist.com.project.log.SysLog;
import nyist.com.project.mapper.SysMenuMapper;
import nyist.com.project.mapper.SysRoleMenuMapper;
import nyist.com.project.result.Result;

@Controller
@RequestMapping("/back/sys")
@Api(value = "", tags="后台菜单接口")
public class SysMenuController {

	@Autowired
	SysMenuMapper sysMenuMapper;
	
	@Autowired
	SysRoleMenuMapper sysRoleMenuMapper; 
	
	@ApiOperation(value = "后台菜单页面", notes = "加载当所有菜单列表页面跳转")
	@RequiresPermissions("sys:menu:list")
	@GetMapping("/menu/menuManage")
	public String toSysMenu(){
		return "back/sys/menuManage";
	}
	
	/**
	 * 菜单管理列表
	 * @return
	 */
	@ApiOperation(value = "后台菜单页面", notes = "加载当所有菜单列表")
	@SysLog(operModule="菜单管理",operType=OperTypeCons.VIEW,operDesc="查看菜单管理列表")
	@RequiresPermissions("sys:menu:list")
	@GetMapping("/menu/getMenu")
	@ResponseBody
	public Result getSysMenu(){
		//List<SysMenuEntity> list = sysMenuMapper.selectAll();
		List<SysMenuEntity> list = sysMenuMapper.selectByLevel("1");//一级菜单
		list.forEach(menu->{
			List<SysMenuEntity> childs = sysMenuMapper.selectByPid(menu.getId());
			childs.forEach(child->{
				List<SysMenuEntity> childsTwo = sysMenuMapper.selectByPid(child.getId());//三级按钮
				child.setChilds(childsTwo);
			});
			menu.setChilds(childs);//二级按钮
		});
		return Result.ok(list);
	}

	
	/**
	 * 删除菜单
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "后台菜单", notes = "根据id删除菜单")
	@SysLog(operModule="菜单管理",operType=OperTypeCons.DEL,operDesc="菜单删除")
	@RequiresPermissions("sys:menu:del")
	@GetMapping("/menu/delMenuById")
	@ResponseBody
	public Result delMenuById(@RequestParam Integer id){
		if(StringUtils.isEmpty(id)){
			return Result.fail();
		}
		//是否有子节点
		List<SysMenuEntity> selectByPid = sysMenuMapper.selectByPid(id);
		if(selectByPid.size()>0){
			return Result.fail("请先移除子节点");
		}
		sysMenuMapper.deleteByPrimaryKey(id);//删除菜单
		sysRoleMenuMapper.deleteByMenuId(id);//删除角色菜单表对应数据
		return Result.ok();
	}
	
	/**
	 * 菜单添加
	 * @param menu
	 * @return
	 */
	@ApiOperation(value = "后台菜单", notes = "添加菜单")
	@SysLog(operModule="菜单管理",operType=OperTypeCons.ADD,operDesc="菜单添加")
	@RequiresPermissions("sys:menu:add")
	@PostMapping("/menu/addMenu")
	@ResponseBody
	public Result addMenu(@RequestBody SysMenuEntity menu){
		menu.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		sysMenuMapper.insertSelective(menu);
		return Result.ok();
	}
	
	/**
	 * 修改菜单获取当前信息
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "后台菜单", notes = "根据id获取当前菜单信息")
	@RequiresPermissions("sys:menu:edit")
	@GetMapping("/menu/getMenuById")
	@ResponseBody
	public Result updateMenu(@RequestParam Integer id){
		SysMenuEntity menu = sysMenuMapper.selectByPrimaryKey(id);
		return Result.ok(menu);
	}
	
	/**
	 * 菜单修改
	 * @param menu
	 * @return
	 */
	@ApiOperation(value = "后台菜单", notes = "修改当前菜单信息")
	@SysLog(operModule="菜单管理",operType=OperTypeCons.EDIT,operDesc="修改菜单信息")
	@RequiresPermissions("sys:menu:edit")
	@PostMapping("/menu/updateMenu")
	@ResponseBody
	public Result updateMenu(@RequestBody SysMenuEntity menu){
		menu.setUpdateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		sysMenuMapper.updateByPrimaryKeySelective(menu);
		return Result.ok();
	}
	
	
	/**
	 * 测试
	 * @return
	 */
	@GetMapping("/sys/testurl")
	public String testurl(){
		return "back/sys/testurl";
	}
	
	
}
