package nyist.com.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import nyist.com.project.entity.SysRoleMenuEntity;
import tk.mybatis.mapper.common.Mapper;

public interface SysRoleMenuMapper extends Mapper<SysRoleMenuEntity> {

	@Delete(" delete from sys_role_menu where role_id = #{roleId}")
	int deleteByRoleId(@Param("roleId")String roleId);

	/**
	 * 根据角色id和菜单id查找
	 * @param roleId
	 * @param menuIds
	 * @return
	 */
	@Insert(" insert into sys_role_menu(role_id,menu_id) values(#{roleId},#{menuid}) ")
	int insertByRoleIdAndMeunId(@Param("roleId")String roleId, @Param("menuid")String menuid);

	/**
	 * 根据角色id查找
	 * @param roleId
	 * @return
	 */
	@Select(" select * from sys_role_menu where role_id= #{roleId} ")
	List<SysRoleMenuEntity> selectByRoleId(@Param("roleId")String roleId);

	@Delete(" delete from sys_role_menu where menu_id = #{id}")
	int deleteByMenuId(@Param("id")Integer id);

}
