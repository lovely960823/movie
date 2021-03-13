package nyist.com.project.service;

import java.util.List;

import nyist.com.project.entity.SysGlobalLogEntity;

public interface SysGlobalLogService {

	List<SysGlobalLogEntity> selectGlobalLogByParams(SysGlobalLogEntity globalLog);

}
