package nyist.com.project.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nyist.com.project.entity.Order;
import nyist.com.project.mapper.OrderMapper;
import nyist.com.project.service.SysOrderService;

@Service
public class SysOrderServiceImpl implements SysOrderService{

	@Autowired
	OrderMapper orderMapper;

	@Override
	public List<Order> selectAllOrder(Order order) {
		
		return orderMapper.selectAllOrder(order);
	}
}
