package nyist.com.project.service;

import java.util.List;


import nyist.com.project.entity.SysLogEntity;

public interface SysLogService {

	/**
	 * 条件查询操作日志
	 * @param sysLogEntity
	 * @return
	 */
	List<SysLogEntity> selectSysLogByParams(SysLogEntity sysLogEntity);

}
