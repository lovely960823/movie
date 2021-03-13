package nyist.com.project.lucene;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import nyist.com.project.entity.SysRoom;
import nyist.com.project.mapper.SysRoomMapper;
import nyist.com.project.result.Result;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class SearchController {
	
	@Autowired
	SysRoomMapper sysRoomMapper;
	
	@GetMapping("/back/indexCreate")
	public ModelAndView index(){
		return new ModelAndView("back/hotel/indexCreate");
	}

	@GetMapping("/back/createIndex")
	public Result createIndex(){
		List<SysRoom> list = sysRoomMapper.selectAll();
		File file = new File("D://myIndex//");
		if(file.exists()){
			file.delete();
		}
		try {
			// 创建文档的集合
	        Collection<Document> docs = new ArrayList<>();
	        for(int i=0;i<list.size();i++){
	        	// 创建文档对象
	            Document document = new Document();
	            //StringField会创建索引，但是不会被分词，TextField，即创建索引又会被分词。
	            document.add(new StringField("id", (i+1)+"", Field.Store.YES));
	            document.add(new TextField("name", list.get(i).getName(), Field.Store.YES));
	            document.add(new TextField("price", list.get(i).getPrice(), Field.Store.YES));
	            document.add(new TextField("descri", list.get(i).getDescri(), Field.Store.YES));
	            docs.add(document);
	        }
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
	        return Result.ok("创建索引成功");
		} catch (Exception e) {
			return Result.fail("创建索引失败");
		}
	}
	
	
	
	/**
	 * 索引数据查询
	 */
	private String[] str={"name","descri","price"};
	@RequestMapping("/indexSearch")
	public String indexSearch(String text,HttpServletRequest request) throws Exception{
		int page=1;
		int pageSize=10;
		Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("D://myIndex//"));
        // 索引读取工具
        IndexReader reader = DirectoryReader.open(directory);
        // 索引搜索工具
        IndexSearcher searcher = new IndexSearcher(reader);
        // 创建查询解析器,两个参数：默认要查询的字段的名称，分词器
        MultiFieldQueryParser  parser = new MultiFieldQueryParser(str, new MyIKAnalyzer());
        // 创建查询对象
        Query query = parser.parse(text);
        // 获取前十条记录
        //TopDocs topDocs = searcher.search(query, 100);
        
        TopDocs topDocs = searchByPage(page,pageSize,searcher,query);
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
            SysRoom sysRoom = sysRoomMapper.selectByPrimaryKey(doc.get("id"));
            //处理高亮字段显示
            String name = highlighter.getBestFragment(new MyIKAnalyzer(), "name",doc.get("name"));
            if(name==null){
            	name=sysRoom.getName();
            }
            String descri = highlighter.getBestFragment(new MyIKAnalyzer(), "descri",doc.get("descri"));
            if(descri==null){
            	descri=sysRoom.getDescri();
            }
            String price = highlighter.getBestFragment(new MyIKAnalyzer(), "price",doc.get("price"));
            sysRoom.setName(name);
            sysRoom.setDescri(descri);
            sysRoom.setPrice(price);
            list.add(sysRoom);
        }
        System.err.println("list的长度："+list.size());
        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("list",list);
        return "index";
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
}
