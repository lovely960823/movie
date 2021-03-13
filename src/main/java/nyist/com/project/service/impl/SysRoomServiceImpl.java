package nyist.com.project.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import nyist.com.project.entity.SysRoom;
import nyist.com.project.mapper.SysRoomMapper;
import nyist.com.project.service.SysRoomService;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class SysRoomServiceImpl implements SysRoomService {

	@Autowired
	private SysRoomMapper sysRoomMapper;
	
	@Override
	public List<SysRoom> selectAll(SysRoom sysRoom) {
		Example example = new Example(sysRoom.getClass());
		example.orderBy("id").desc();
		Criteria createCriteria = example.createCriteria();
		if(!StringUtils.isEmpty(sysRoom.getName())){
			createCriteria.andLike("name", sysRoom.getName());
		}
		if(!StringUtils.isEmpty(sysRoom.getType())){
			createCriteria.andEqualTo("type", sysRoom.getType());
		}
		if(!StringUtils.isEmpty(sysRoom.getStatus())){
			createCriteria.andEqualTo("status", sysRoom.getStatus());
		}
		return sysRoomMapper.selectByExample(example);
	}

	@Override
	public List<SysRoom> selectAllUsing(SysRoom sysRoom) {
		Example example = new Example(sysRoom.getClass());
		example.orderBy("id").desc();
		Criteria createCriteria = example.createCriteria();
		if(!StringUtils.isEmpty(sysRoom.getName())){
			createCriteria.andLike("name", sysRoom.getName());
		}
		if(!StringUtils.isEmpty(sysRoom.getType())){
			createCriteria.andEqualTo("type", sysRoom.getType());
		}
		createCriteria.andEqualTo("status", "2");
		return sysRoomMapper.selectByExample(example);
	}

}
