package nyist.com.project.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import nyist.com.project.entity.SysLogEntity;
import nyist.com.project.mapper.SysLogMapper;
import nyist.com.project.service.SysLogService;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class SysLogServiceImpl implements SysLogService {

	@Autowired
	SysLogMapper sysLogMapper;

	@Override
	public List<SysLogEntity> selectSysLogByParams(SysLogEntity sysLogEntity) {
		Example example = new Example(sysLogEntity.getClass());
		example.orderBy("id").desc();
		Criteria createCriteria = example.createCriteria();
		if(!StringUtils.isEmpty(sysLogEntity.getBeginDate())){
			createCriteria.andGreaterThanOrEqualTo("createDate", sysLogEntity.getBeginDate());
		}
		if(!StringUtils.isEmpty(sysLogEntity.getEndDate())){
			createCriteria.andLessThanOrEqualTo("createDate", sysLogEntity.getEndDate());
		}
		return sysLogMapper.selectByExample(example);
	}

}
