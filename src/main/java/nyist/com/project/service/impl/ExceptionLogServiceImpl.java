package nyist.com.project.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import nyist.com.project.entity.ExceptionLogEntity;
import nyist.com.project.mapper.ExceptionLogMapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class ExceptionLogServiceImpl implements nyist.com.project.service.ExceptionLogService {

	@Autowired
	ExceptionLogMapper exceptionLogMapper;

	@Override
	public List<ExceptionLogEntity> selectExcLogByParams(ExceptionLogEntity exceptionLogEntity) {
		Example example = new Example(exceptionLogEntity.getClass());
		example.orderBy("id").desc();
		Criteria createCriteria = example.createCriteria();
		if(!StringUtils.isEmpty(exceptionLogEntity.getBeginDate())){
			createCriteria.andGreaterThanOrEqualTo("createTime", exceptionLogEntity.getBeginDate());
		}
		if(!StringUtils.isEmpty(exceptionLogEntity.getEndDate())){
			createCriteria.andLessThanOrEqualTo("createTime", exceptionLogEntity.getEndDate());
		}
		return exceptionLogMapper.selectByExample(example);
	}
}
