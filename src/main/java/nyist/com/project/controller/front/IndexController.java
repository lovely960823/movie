package nyist.com.project.controller.front;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.wxpay.sdk.WXPayUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nyist.com.project.config.PayProperties;
import nyist.com.project.entity.Order;
import nyist.com.project.entity.SysRoom;
import nyist.com.project.entity.SysUserEntity;
import nyist.com.project.lucene.MyIKAnalyzer;
import nyist.com.project.mapper.OrderMapper;
import nyist.com.project.mapper.SysRoomMapper;
import nyist.com.project.result.Result;

@RestController
@Api(value="", tags="前台首页接口")
public class IndexController {
	
	@Autowired
	SysRoomMapper sysRoomMapper;
	
	@Autowired
	PayProperties payProperties;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	OrderMapper orderMapper;

	@GetMapping("/")
	public ModelAndView frontIndex() {
		return new ModelAndView("front/index");
	}
	
	/**
	 * 首页房间列表
	 * @return
	 * @throws Exception 
	 * @throws ParseException
	 */
	@ApiOperation(value = "数据列表", notes = "前台页面房间数据列表")
	@PostMapping("/roomList")
	public Result roomList(@RequestBody SysRoom sysRoom) throws Exception{
		PageHelper.startPage(sysRoom.getPageNum(),sysRoom.getPageSize());//要放在上面，才会按照你的要求数据查询
		if(StringUtils.isEmpty(sysRoom.getSearchName())){
			List<SysRoom>list = sysRoomMapper.selectAllRooms(sysRoom);
			PageInfo<SysRoom> pageInfo = new PageInfo<SysRoom>(list);//new具体实现的时候，一定要把数据类型带上
			pageInfo.setList(list);
			return Result.ok(pageInfo);
		}else{
			String[] str={"name","descri","price"};
			Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("C://myIndex//"));
	        // 索引读取工具
	        IndexReader reader = DirectoryReader.open(directory);
	        // 索引搜索工具
	        IndexSearcher searcher = new IndexSearcher(reader);
	        // 创建查询解析器,两个参数：默认要查询的字段的名称，分词器
	        MultiFieldQueryParser  parser = new MultiFieldQueryParser(str, new MyIKAnalyzer());
	        // 创建查询对象
	        Query query = parser.parse(sysRoom.getSearchName());
	        // 获取前十条记录
	        //TopDocs topDocs = searcher.search(query, 100);
	        
	        TopDocs topDocs = searchByPage(sysRoom.getPageNum(),sysRoom.getPageSize(),searcher,query);
	        // 获取总条数
	        System.out.println("本次搜索共找到" + topDocs.totalHits + "条数据");
	        
	        //高亮显示
	        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
	        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
	        Fragmenter fragmenter = new SimpleFragmenter(100);   //高亮后的段落范围在100字内
	        highlighter.setTextFragmenter(fragmenter);

	        // 获取得分文档对象（ScoreDoc）数组.SocreDoc中包含：文档的编号、文档的得分
	        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	        List<SysRoom> list = new ArrayList<>();
	        for (ScoreDoc scoreDoc : scoreDocs) {
	            // 取出文档编号
	            int docID = scoreDoc.doc;
	            // 根据编号去找文档
	            Document doc = reader.document(docID);
	            SysRoom room = sysRoomMapper.selectByPrimaryKey(doc.get("id"));
	            //处理高亮字段显示
	            String name = highlighter.getBestFragment(new MyIKAnalyzer(), "name",doc.get("name"));
	            if(name==null){
	            	name=room.getName();
	            }
	            String descri = highlighter.getBestFragment(new MyIKAnalyzer(), "descri",doc.get("descri"));
	            if(descri==null){
	            	descri=room.getDescri();
	            }
	            String price = highlighter.getBestFragment(new MyIKAnalyzer(), "price",doc.get("price"));
	            if(price==null){
	            	price = room.getPrice();
	            }
	            room.setName(name);
	            room.setDescri(descri);
	            room.setPrice(price);
	            list.add(room);
	        }
	        PageInfo<SysRoom> pageInfo = new PageInfo<SysRoom>(list);//new具体实现的时候，一定要把数据类型带上
	        pageInfo.setTotal(topDocs.totalHits);
			pageInfo.setList(list);
			return Result.ok(pageInfo);
		}
		//List<SysRoom> list = sysRoomMapper.selectAllRooms(sysRoom);
	}
	
	private TopDocs searchByPage(int page,int perPage, IndexSearcher searcher, Query query) throws IOException {
		TopDocs result = null;
		if(query == null){
			System.out.println(" Query is null return null ");
			return null;
		}
		ScoreDoc before = null;
		if(page != 1){
			TopDocs docsBefore = searcher.search(query, (page-1)*perPage);
			ScoreDoc[] scoreDocs = docsBefore.scoreDocs;
			if(scoreDocs.length > 0){
				before = scoreDocs[scoreDocs.length - 1];
			}
		}
		result = searcher.searchAfter(before, query, perPage);
		return result;
	}
	
	/**
	 * 根据id获取房间信息
	 * @param rid
	 * @return
	 */
	@ApiOperation(value = "房间信息", notes = "根据id获取到房间信息")
	@GetMapping("/getRoomById/{rid}")
	public Result getRoomById(@PathVariable Integer rid){
		SysRoom sysRoom = sysRoomMapper.selectByPrimaryKey(rid);
		return Result.ok(sysRoom);
	}
	
	/**
	 * 订单二维码
	 * @param sysRoom
	 * @return
	 */
	@ApiOperation(value = "订单二维码", notes = "下单生成二维码接口")
	@PostMapping("/qrCode")
	public Result qrCode() {
		//https://pay.weixin.qq.com/wiki/doc/api/wxpay_v2/open/chapter6_1.shtml
		String orderId = ""+System.currentTimeMillis()+"";
		Map<String,Object> map = new HashMap<>();
		map.put("orderId", orderId);
		System.err.println("订单id"+orderId);
		 try {            
			 Map<String,String> paramMap = new HashMap<>();            
			 paramMap.put("appid",payProperties.getAppID());            
			 paramMap.put("mch_id",payProperties.getMchID());            
			 paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机数           
			 paramMap.put("body","影院");      
			 paramMap.put("out_trade_no",orderId); //交易号            
			 paramMap.put("total_fee","1"); // 测试时 使用1分钱           
			 paramMap.put("spbill_create_ip","127.0.0.1");            
			 paramMap.put("notify_url",payProperties.getNotifyurl());          
			 paramMap.put("trade_type","NATIVE");            //将参数转xml            
			 String paramXml = WXPayUtil.generateSignedXml(paramMap, payProperties.getKey());            //2、基于httpclient工具类，调用微信支付平台，完成支付操作            
			 String resultString = restTemplate.postForObject("https://api.mch.weixin.qq.com/pay/unifiedorder", paramXml, String.class);            
			 Map<String, String> stringStringMap = WXPayUtil.xmlToMap(resultString); 
			 String code_url = stringStringMap.get("code_url");   
			 System.err.println(orderId+"***"+code_url);
			 map.put("code_url", code_url);
			 return Result.ok(map);
			 } 
		 	catch (Exception e) {    
		 		 return Result.fail("支付流程失败啦！！！");
			}
	}
	
	/**
	 * 订单支付状态
	 * @param orderId
	 * @return
	 */
	@ApiOperation(value = "支付状态", notes = "根据订单id查询支付状态")
	@GetMapping("/payState/{orderId}")
	public Result queryPayState(@PathVariable("orderId") String orderId) { 
		 try {
			// 1、组装微信查询支付状态所需要的必填参数            
			 Map<String,String> paramMap = new HashMap<>();            
			 paramMap.put("appid",payProperties.getAppID());            
			 paramMap.put("mch_id",payProperties.getMchID());            
			 paramMap.put("nonce_str", WXPayUtil.generateNonceStr());            
			 paramMap.put("out_trade_no",orderId.toString());            //将参数转xml
			 String paramXml = WXPayUtil.generateSignedXml(paramMap, payProperties.getKey());            //2、基于restTemplate工具类，调用微信支付平台，完成支付状态操作操作                        
			 String resultString = restTemplate.postForObject("https://api.mch.weixin.qq.com/pay/orderquery", paramXml, String.class);            //3、处理响应结果            
			 Map<String, String> resultMap = WXPayUtil.xmlToMap(resultString);            //支付成功，返回状态1   
			 String status = resultMap.get("trade_state");
			 if(status.equals("NOTPAY")){
				 return Result.fail("暂未支付");
			 }else if(status.equals("SUCCESS")){
				 return Result.ok("支付成功");
			 }else{
				 return Result.fail("发生意外");
			 }
		} catch (Exception e) {
			return Result.fail("发生意外");
		} 
	}
	
	/**
	 * 订单生成
	 * @param orderId
	 * @param sysRoom
	 * @return
	 */
	@ApiOperation(value = "订单生成", notes = "支付成功后生成订单信息")
	@PostMapping("/makeOrder")
	public Result makeOrder(@RequestBody Order  order) {
		order.setId(null);
		//当前时间
		SimpleDateFormat temp=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date=new Date();
		String date2=temp.format(date);
		//获取到第二天中午十二点的时间
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, 1);
		c.set(Calendar.HOUR_OF_DAY, 12);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		SysRoom sysRoom = sysRoomMapper.selectByPrimaryKey(order.getRid());
		sysRoom.setStatus("2");
		sysRoom.setBeginTime(date2);
		sysRoom.setEndTime(df.format(c.getTime()));//设置时间方便查看当天需要退房或者续租使用的
		sysRoomMapper.updateByPrimaryKeySelective(sysRoom);
		
		
		order.setCreateTime(date2);
		order.setPrice(sysRoom.getPrice());
		orderMapper.insertSelective(order);
		return Result.ok();
		
	}
}
