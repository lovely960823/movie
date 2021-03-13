package nyist.com.project.mapper;


import org.apache.ibatis.annotations.Delete;

import io.lettuce.core.dynamic.annotation.Param;
import nyist.com.project.entity.SysGlobalLogEntity;
import tk.mybatis.mapper.common.Mapper;

public interface SysGlobalLogMapper extends Mapper<SysGlobalLogEntity>{

	@Delete(" delete from sys_global_log where create_time<=#{day} ")
	void deleteByTime(@Param("day")String day);

}
