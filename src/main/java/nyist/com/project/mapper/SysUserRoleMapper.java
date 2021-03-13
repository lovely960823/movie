package nyist.com.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import nyist.com.project.entity.SysUserRoleEntity;
import tk.mybatis.mapper.common.Mapper;

public interface SysUserRoleMapper extends Mapper<SysUserRoleEntity>{

	/**
	 * 根据用户id查询对应角色
	 * @param userId
	 * @return
	 */
	@Select(" select * from sys_user_role where user_id=#{userId}")
	List<SysUserRoleEntity> selectByUserId(@Param("userId")Integer userId);

	/**
	 * 根据用户id删除用户角色表中的数据
	 * @param userId
	 * @return
	 */
	@Delete(" delete from sys_user_role where user_id=#{userId} ")
	int deleteByUserId(@Param("userId")Integer userId);

	/**
	 * 根据用户id和对应角色id插入
	 * @param userId
	 * @param roleId
	 * @return
	 */
	@Insert(" insert into sys_user_role(user_id,role_id) values(#{userId},#{roleId}) ")
	int insertByUserIdAndRoleId(@Param("userId")Integer userId, @Param("roleId")String roleId);

}
