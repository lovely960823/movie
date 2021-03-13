package nyist.com.project.controller.back.hotel;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nyist.com.project.entity.SysLogEntity;
import nyist.com.project.entity.SysRoom;
import nyist.com.project.lucene.MyIKAnalyzer;
import nyist.com.project.mapper.SysRoomMapper;
import nyist.com.project.result.Result;
import nyist.com.project.service.SysRoomService;

/**
 * 房间管理
 * @author Administrator
 *
 */
@RestController
@Api(value="", tags="房间管理接口")
@RequestMapping("/back/hotel")
public class RoomController {
	
	@Value("${myUploadPath}")
	private String myUploadPath;
	
	@Autowired
	private SysRoomService sysRoomService;
	@Autowired
	private SysRoomMapper sysRoomMapper;

	
	@ApiOperation(value = "房间页面跳转", notes = "跳转到房间列表页面")
	@GetMapping("/room/roomManage")
	@RequiresPermissions("hotel:room:view")
	public ModelAndView toRoomManage() {
		//获取到第二天十二点的时间
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, 1);
		c.set(Calendar.HOUR_OF_DAY, 12);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		System.err.println(df.format(c.getTime()));
		//日期转为时间戳
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date sjc = sf.parse(df.format(c.getTime()));
			long time = sjc.getTime();
			System.err.println(time);
		} catch (ParseException e) {
		}
		return new ModelAndView("back/hotel/room/roomManage");
	}
	
	
	/**
	 * 房间数据列表
	 * @param sysRoom
	 * @return
	 */
	@ApiOperation(value = "房间数据列表", notes = "加载房间列表数据")
	@PostMapping("/room/roomManage")
	@RequiresPermissions("hotel:room:view")
	public Result roomList(@RequestBody SysRoom sysRoom) {
		PageHelper.startPage(sysRoom.getPageNum(),sysRoom.getPageSize());//要放在上面，才会按照你的要求数据查询
		List<SysRoom>list = sysRoomService.selectAll(sysRoom);
		PageInfo<SysRoom> pageInfo = new PageInfo<SysRoom>(list);//new具体实现的时候，一定要把数据类型带上
	    pageInfo.setList(list);
		return Result.ok(pageInfo);
	}
	
	
	/**
	 * 房间添加或者修改
	 * @param sysRoom
	 * @return
	 */
	@PostMapping("/room/roomAdd")
	@ApiOperation(value = "房间增加或者修改", notes = "如果id存在则为添加，否则为修改")
	//@RequiresPermissions("hotel:room:add")
	public Result roomAdd(@RequestBody SysRoom sysRoom) throws Exception{
		if(sysRoom.getId()==null) {
			//新增
			SysRoom s = sysRoomMapper.selectByName(sysRoom.getName());
			if(s!=null) {
				return Result.fail("该房间已经存在！");
			}
			sysRoom.setStatus("1");
			sysRoomMapper.insertSelective(sysRoom);
			//新增房间的时候添加索引
			SysRoom selectOne = sysRoomMapper.selectOne(sysRoom);
			// 创建文档的集合
	        Collection<Document> docs = new ArrayList<>();
        	// 创建文档对象
            Document document = new Document();
            //StringField会创建索引，但是不会被分词，TextField，即创建索引又会被分词。
            document.add(new StringField("id", selectOne.getId()+"", Field.Store.YES));
            document.add(new TextField("name", selectOne.getName(), Field.Store.YES));
            document.add(new TextField("price", selectOne.getPrice(), Field.Store.YES));
            document.add(new TextField("descri", selectOne.getDescri(), Field.Store.YES));
            docs.add(document);
	        // 索引目录类,指定索引在硬盘中的位置，我的设置为D盘的indexDir文件夹
	        Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("C://myIndex//"));
	        // 引入IK分词器
	        Analyzer analyzer = new MyIKAnalyzer();
	        // 索引写出工具的配置对象，这个地方就是最上面报错的问题解决方案
	        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
	        // 设置打开方式：OpenMode.APPEND 会在索引库的基础上追加新索引。OpenMode.CREATE会先清空原来数据，再提交新的索引
	        conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
	        // 创建索引的写出工具类。参数：索引的目录和配置信息
	        IndexWriter indexWriter = new IndexWriter(directory, conf);
	        // 把文档集合交给IndexWriter
	        indexWriter.addDocuments(docs);
	        // 提交
	        indexWriter.commit();
	        // 关闭
	        indexWriter.close();
			return Result.ok("添加成功");
		}else {
			sysRoomMapper.updateByPrimaryKeySelective(sysRoom);
			//更新索引
			 Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("C://myIndex//"));
		     // 创建配置对象
		     IndexWriterConfig conf = new IndexWriterConfig(new MyIKAnalyzer());
		     // 创建索引写出工具
		     IndexWriter writer = new IndexWriter(directory, conf);
		     // 创建新的文档数据
		     Document doc = new Document();
		     doc.add(new StringField("id", sysRoom.getId()+"", Field.Store.YES));
		     doc.add(new TextField("name", sysRoom.getName(), Field.Store.YES));
		     doc.add(new TextField("price", sysRoom.getPrice(), Field.Store.YES));
             doc.add(new TextField("descri", sysRoom.getDescri(), Field.Store.YES));
		     writer.updateDocument(new Term("id",sysRoom.getId()+""), doc);
		     // 提交
		     writer.commit();
		     // 关闭
		     writer.close();
			return Result.ok("修改成功");
		}
	}
	
	/**
	 * 房间删除根据id
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "房间删除", notes = "根据房间id进行删除操作")
	@GetMapping("/room/delById/{id}")
	@RequiresPermissions("hotel:room:del")
	public Result delById(@PathVariable("id")Integer id) throws Exception{
		sysRoomMapper.deleteByPrimaryKey(id);
		// 创建目录对象
   	 	Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("C://myIndex//"));
	     // 创建配置对象
	     IndexWriterConfig conf = new IndexWriterConfig(new IKAnalyzer());
	     // 创建索引写出工具
	     IndexWriter writer = new IndexWriter(directory, conf);
	     // 根据词条进行删除
	     writer.deleteDocuments(new Term("id", id+""));
	     // 提交
	     writer.commit();
	     // 关闭
	     writer.close();
		
		return Result.ok();
	}
	
	
	//房间退房或者续租的处理*****************************************************
	@GetMapping("/room/usingManage")
	public ModelAndView toUserManage() {
		return new ModelAndView("back/hotel/room/usingManage");
	}
	
	/**
	 * 获取到所有正在使用中的房间，包间数据列表
	 * @param sysRoom
	 * @return
	 */
	@ApiOperation(value = "包间数据列表", notes = "加载当前正在消费的包间信息")
	@PostMapping("/room/usingManage")
	@RequiresPermissions("hotel:bj:view")
	public Result usingRoomList(@RequestBody SysRoom sysRoom) {
		PageHelper.startPage(sysRoom.getPageNum(),sysRoom.getPageSize());//要放在上面，才会按照你的要求数据查询
		List<SysRoom>list = sysRoomService.selectAllUsing(sysRoom);
		PageInfo<SysRoom> pageInfo = new PageInfo<SysRoom>(list);//new具体实现的时候，一定要把数据类型带上
	    pageInfo.setList(list);
		return Result.ok(pageInfo);
	}
	
	
	/**
	 * 退房
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "包间退房", notes = "")
	@RequiresPermissions("hotel:room:tuifang")
	@GetMapping("/room/tfById/{id}")
	public Result tuifang(@PathVariable("id")Integer id){
		sysRoomMapper.tfById(id);
		return Result.ok("退房成功");
	}
	
	
	@ApiOperation(value = "房间上传图片", notes = "")
	@RequiresPermissions("hotel:room:upload")
	@PostMapping("/room/uploadFile")
    public Result uploadFile(MultipartFile file,@RequestParam("id") String id) throws IOException {
        if(StringUtils.isEmpty(id)){
            return Result.fail("id不能为空");
        }
        if(file==null){
            return Result.fail("文件不能为空");
        }
        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1 );
        String newFileName = UUID.randomUUID().toString() + "." + ext;
        file.transferTo(new File(myUploadPath+newFileName));
        SysRoom myUser = sysRoomMapper.selectByPrimaryKey(id);
        if(!StringUtils.isEmpty(myUser.getProfile())){
            File file1 = new File(myUploadPath+myUser.getProfile());
            if(file1.exists()){
                file1.delete();
            }
        }
        myUser.setProfile(newFileName);
        sysRoomMapper.updateByPrimaryKeySelective(myUser);
        return Result.ok();
    }
	
}
