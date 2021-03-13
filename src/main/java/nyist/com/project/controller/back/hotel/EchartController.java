package nyist.com.project.controller.back.hotel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.Api;
import nyist.com.project.entity.Order;
import nyist.com.project.entity.SysRoom;
import nyist.com.project.mapper.OrderMapper;
import nyist.com.project.mapper.SysRoomMapper;

@RestController
@Api(value="", tags="数据统计接口")
@RequestMapping("/back/hotel")
public class EchartController {
	
	@Autowired
	SysRoomMapper sysRoomMapper;
	
	@Autowired
	OrderMapper orderMapper;
	
	@GetMapping("/echart")
	public ModelAndView echart(HttpServletRequest request) {
		Map<String,Object> data = new HashMap<String, Object>();
		List<Integer> dataY = new ArrayList<Integer>();
		List<SysRoom> roomList = sysRoomMapper.selectAll();
		roomList.forEach(room->{
			List<Order> orderList =  orderMapper.selectOrdersByRid(room.getId());
			dataY.add(orderList.size());
		});
		List<String> collect = roomList.stream().map(SysRoom::getName).collect(Collectors.toList());
		data.put("dataX", collect);
		data.put("dataXY", dataY);
		System.err.println(data.toString());
		request.setAttribute("data", data);
		return new ModelAndView("back/hotel/echart/echart");
	}
	
	@GetMapping("/getEchart")
	public Map<String,Object> getData(){
		Map<String,Object> data = new HashMap<String, Object>();
		List<Integer> dataY = new ArrayList<Integer>();
		List<SysRoom> roomList = sysRoomMapper.selectAll();
		roomList.forEach(room->{
			List<Order> orderList =  orderMapper.selectOrdersByRid(room.getId());
			dataY.add(orderList.size());
		});
		List<String> collect = roomList.stream().map(SysRoom::getName).collect(Collectors.toList());
		data.put("dataX", collect);
		data.put("dataY", dataY);
		return data;
	}

}
