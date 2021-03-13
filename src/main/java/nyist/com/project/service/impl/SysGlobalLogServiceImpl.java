package nyist.com.project.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import nyist.com.project.entity.SysGlobalLogEntity;
import nyist.com.project.mapper.SysGlobalLogMapper;
import nyist.com.project.service.SysGlobalLogService;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class SysGlobalLogServiceImpl implements SysGlobalLogService {

	@Autowired
	private SysGlobalLogMapper sysGlobalLogMapper;
	
	@Override
	public List<SysGlobalLogEntity> selectGlobalLogByParams(SysGlobalLogEntity sysGlobalLog) {
		Example example = new Example(sysGlobalLog.getClass());
		example.orderBy("id").desc();
		Criteria createCriteria = example.createCriteria();
		if(!StringUtils.isEmpty(sysGlobalLog.getBeginDate())){
			createCriteria.andGreaterThanOrEqualTo("createTime", sysGlobalLog.getBeginDate());
		}
		if(!StringUtils.isEmpty(sysGlobalLog.getEndDate())){
			createCriteria.andLessThanOrEqualTo("createTime", sysGlobalLog.getEndDate());
		}
		return sysGlobalLogMapper.selectByExample(example);
	}
}
