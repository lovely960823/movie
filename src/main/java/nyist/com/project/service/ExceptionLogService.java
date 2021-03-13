package nyist.com.project.service;

import java.util.List;


import nyist.com.project.entity.ExceptionLogEntity;

public interface ExceptionLogService {

	List<ExceptionLogEntity> selectExcLogByParams(ExceptionLogEntity exceptionLogEntity);

}
