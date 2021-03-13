package nyist.com.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import nyist.com.project.entity.SysRoom;
import tk.mybatis.mapper.common.Mapper;

public interface SysRoomMapper extends Mapper<SysRoom>{

	@Select(" select * from sys_room where name=#{name} ")
	SysRoom selectByName(@Param("name")String name);

	/**
	 * 空闲的排在最前面
	 * 小号排在前面
	 * @return
	 */
	List<SysRoom> selectAllRooms(@Param("sysRoom")SysRoom sysRoom);

	@Update(" update sys_room set status='1',begin_time=null,end_time=null where id=#{id} ")
	void tfById(@Param("id")Integer id);

}
