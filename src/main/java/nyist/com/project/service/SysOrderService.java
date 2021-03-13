package nyist.com.project.service;

import java.util.List;

import nyist.com.project.entity.Order;

public interface SysOrderService {

	List<Order> selectAllOrder(Order order);

}
