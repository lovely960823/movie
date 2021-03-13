package nyist.com.project.controller.back.sys;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.Api;
import nyist.com.project.entity.ExceptionLogEntity;
import nyist.com.project.entity.SysGlobalLogEntity;
import nyist.com.project.entity.SysLogEntity;
import nyist.com.project.log.GlobalLog;
import nyist.com.project.result.Result;
import nyist.com.project.service.ExceptionLogService;
import nyist.com.project.service.SysGlobalLogService;
import nyist.com.project.service.SysLogService;

@Controller
@RequestMapping("/back/sys")
@Api(value = "", tags="后台日志接口")
public class SysLogController {

	@Autowired
	SysLogService sysLogService;
	
	@Autowired
	ExceptionLogService exceptionLogService;
	
	@Autowired
	SysGlobalLogService globalLogService;
	
	@GetMapping("/log/toSysLog")
	@RequiresPermissions("sys:operlog:view")
	public String toOperLog(){
		return "back/sys/sysLogManage";
	}
	
	/*
	 * 自定义日志
	 */
	@PostMapping("/log/sysLog")
	@ResponseBody
	@RequiresPermissions("sys:operlog:view")
	public Result operLog(@RequestBody SysLogEntity sysLogEntity){
		PageHelper.startPage(sysLogEntity.getPageNum(),sysLogEntity.getPageSize());//要放在上面，才会按照你的要求数据查询
        List<SysLogEntity> list = sysLogService.selectSysLogByParams(sysLogEntity);
        PageInfo<SysLogEntity> pageInfo = new PageInfo<SysLogEntity>(list);//new具体实现的时候，一定要把数据类型带上
        pageInfo.setList(list);
        return Result.ok(pageInfo);
	}
	
	@GetMapping("/log/toExcLog")
	@RequiresPermissions("sys:exc:view")
	public String toExcLog(){
		return "back/sys/sysExcManage";
	}
	
	/**
	 * 异常日志
	 * @param exceptionLogEntity
	 * @return
	 */
	@PostMapping("/log/excLog")
	@ResponseBody
	@RequiresPermissions("sys:exc:view")
	public Result excLog(@RequestBody ExceptionLogEntity  exceptionLogEntity){
		PageHelper.startPage(exceptionLogEntity.getPageNum(),exceptionLogEntity.getPageSize());//要放在上面，才会按照你的要求数据查询
        List<ExceptionLogEntity> list = exceptionLogService.selectExcLogByParams(exceptionLogEntity);
        PageInfo<ExceptionLogEntity> pageInfo = new PageInfo<ExceptionLogEntity>(list);//new具体实现的时候，一定要把数据类型带上
        pageInfo.setList(list);
        return Result.ok(pageInfo);
	}
	
	
	@GetMapping("/log/toGlobalLog")
	@RequiresPermissions("sys:global:view")
	public String toGlobalLog(){
		return "back/sys/sysGlobalManage";
	}
	
	/**
	 * 全局日志
	 * @param globalLog
	 * @return
	 */
	@PostMapping("/log/globalLog")
	@ResponseBody
	@RequiresPermissions("sys:global:view")
	public Result globalLog(@RequestBody SysGlobalLogEntity  globalLog){
		PageHelper.startPage(globalLog.getPageNum(),globalLog.getPageSize());//要放在上面，才会按照你的要求数据查询
        List<SysGlobalLogEntity> list = globalLogService.selectGlobalLogByParams(globalLog);
        PageInfo<SysGlobalLogEntity> pageInfo = new PageInfo<SysGlobalLogEntity>(list);//new具体实现的时候，一定要把数据类型带上
        pageInfo.setList(list);
        return Result.ok(pageInfo);
	}
}
