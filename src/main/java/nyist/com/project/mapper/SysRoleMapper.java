package nyist.com.project.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import nyist.com.project.entity.SysRoleEntity;
import tk.mybatis.mapper.common.Mapper;

public interface SysRoleMapper extends Mapper<SysRoleEntity> {

	/**
	 * 根据角色名称查找
	 * @param name
	 * @return
	 */
	@Select(" select * from sys_role where name = #{name} limit 1")
	SysRoleEntity selectByName(@Param("name")String name);

}
