package nyist.com.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import nyist.com.project.entity.SysMenuEntity;
import tk.mybatis.mapper.common.Mapper;

public interface SysMenuMapper extends Mapper<SysMenuEntity>{
	/**
	 * 将角色写进用户表版本
	 *  select distinct m.* from sys_menu m 
		inner join sys_role_menu rm on m.id=rm.menu_id  
		inner join sys_role r on  r.id = rm.role_id 
		LEFT JOIN sys_user u on r.name= u.roles where r.name in('管理员','普通用户');
	 * @param roles
	 * @return
	 */
	//@Select(" select s.* from sys_menu s where s.id in(SELECT m.menu_id from sys_role_menu  m where role_id in(select id from sys_role where name=#{roles})) ")
	/*@Select(" select distinct m.* from sys_menu m left join sys_role_menu rm on m.id=rm.menu_id left join sys_role r on  r.id = rm.role_id LEFT JOIN sys_user u on r.name= u.roles where FIND_IN_SET(r.name,#{roles}) ")
	List<SysMenuEntity> findAllPer(@Param("roles")String roles);*/
	
	/**
	 * 用户角色单独存放一张表
	 * select distinct m.* from sys_menu m 
		inner join sys_role_menu rm on m.id=rm.menu_id  
		inner join sys_role r on  r.id = rm.role_id 
		inner join sys_user_role ur on ur.role_id = r.id
		inner join sys_user  u on u.id= ur.user_id where u.id=3
	 * @param level
	 * @return
	 */
	
	@Select(" select distinct m.* from sys_menu m inner join sys_role_menu rm on m.id=rm.menu_id inner join sys_role r on  r.id = rm.role_id inner join sys_user_role ur on ur.role_id = r.id inner join sys_user  u on u.id= ur.user_id where u.id=#{userId} order by m.sorts desc ")
	List<SysMenuEntity> findAllPer(@Param("userId")Integer userId);

	@Select(" select * from sys_menu where level=#{level} order by sorts desc ")
	List<SysMenuEntity> selectByLevel(@Param("level")String level);

	@Select(" select * from sys_menu where pid=#{id} order by sorts desc ")
	List<SysMenuEntity> selectByPid(@Param("id")Integer id);

}
