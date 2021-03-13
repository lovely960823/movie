package nyist.com.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import nyist.com.project.entity.Order;
import tk.mybatis.mapper.common.Mapper;

public interface OrderMapper extends Mapper<Order>{

	List<Order> selectAllOrder(@Param("order") Order order);

	@Select(" select * from sys_order where rid =#{id} order by id desc limit 1 ")
	Order selectByRid(@Param("id")Integer id);

	@Select(" select * from sys_order where rid =#{id} ")
	List<Order> selectOrdersByRid(int i);

}
