package nyist.com.project.service;

import java.util.List;

import nyist.com.project.entity.SysRoom;

public interface SysRoomService {

	List<SysRoom> selectAll(SysRoom sysRoom);

	List<SysRoom> selectAllUsing(SysRoom sysRoom);

}
