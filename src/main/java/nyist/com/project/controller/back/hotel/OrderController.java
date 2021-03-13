package nyist.com.project.controller.back.hotel;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nyist.com.project.entity.Order;
import nyist.com.project.entity.SysRoom;
import nyist.com.project.mapper.OrderMapper;
import nyist.com.project.result.Result;
import nyist.com.project.service.SysOrderService;

@RestController
@Api(value="", tags="订单管理接口")
@RequestMapping("/back/hotel")
public class OrderController {
	
	@Autowired
	private SysOrderService sysOrderService;
	@Autowired
	private OrderMapper orderMapper;
	
	@ApiOperation(value = "订单列表界面", notes = "跳转到订单列表页面")
	@GetMapping("/order/orderManage")
	public ModelAndView toorderManage() {
		return new ModelAndView("back/hotel/order/orderManage");
	}
	
	/**
	 * 订单列表
	 * @param order
	 * @return
	 */
	@ApiOperation(value = "订单列表", notes = "查询所有的订单数据")
	@PostMapping("/order/orderManage")
	@RequiresPermissions("hotel:order:view")
	public Result orderList(@RequestBody Order order){
		//处理一下日期
		if(StringUtils.isNotEmpty(order.getCreateTime())){
			order.setCreateTime(order.getCreateTime().substring(0, 10));
		}
		PageHelper.startPage(order.getPageNum(),order.getPageSize());//要放在上面，才会按照你的要求数据查询
		List<Order>list = sysOrderService.selectAllOrder(order);
		PageInfo<Order> pageInfo = new PageInfo<Order>(list);//new具体实现的时候，一定要把数据类型带上
	    pageInfo.setList(list);
		return Result.ok(pageInfo);
		
	}
	
	/**
	 * 根据id删除
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "订单删除", notes = "根据id删除订单")
	@RequiresPermissions("hotel:order:del")
	@GetMapping("/order/delById/{id}")
	public Result delById(@PathVariable("id")Integer id){
		orderMapper.deleteByPrimaryKey(id);
		return Result.ok();
	}
	
	/**
	 * 根据房间id获取到最新的订单信息
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "订单详情", notes = "续租的时候根据房间id获取到当前用户信息")
	@GetMapping("/order/getById/{id}")
	public Result getById(@PathVariable("id")Integer id){
		Order order = orderMapper.selectByRid(id);
		return Result.ok(order);
	}

}
