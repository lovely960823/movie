package nyist.com.project.cache;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import nyist.com.project.config.AppGetBean;
/**
 * 使用注意事项：1、在【只有单表操作】的表上使用缓存
 * 			2、在可以保证查询远远大于insert,update,delete操作的情况下使用缓存
 * Redis自定义mybatis的cache缓存
 * 注意：1、实体类要实现序列化
 * 	   2、缓存开启在xml文件里面(不走xml不入缓存) <cache type="nyist.com.project.cache.MybatisCache"></cache>  如果有关联表使用这种   <cache-ref namespace="" />关联缓存，比如用户表关联角色表，可以在角色表使用该标签，namespace指向用户Mapper
 * 	   3、针对mapper类开启@CacheNamespace(implementation = MybatisCache.class) 指定实现类
 * 	   4、mapper和xml同时起效：接口加注解 CacheNamespaceRef(UserMapper.class)   xml使用cache标签
 * @author ljw
 *
 */
public class MyRedisCache implements Cache{

	//获取redisTemplate 模板 
	@SuppressWarnings("unchecked")
	private RedisTemplate<String,Object> getStringRedisTemplate(){
		return (RedisTemplate<String,Object>) AppGetBean.getBean("redisTemplate");
	}
	
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	 
	private String id;//namespace
	
	/**
	 * 必须要实例化一个id
	 * @param id
	 */
	public MyRedisCache(String id){
		this.id=id;
	}
	
	@Override
	public String getId() {
		return id;
	}

	/**
	 * 放入缓存
	 * key namespace+mapper的id
	 * value 结果集
	 * 需要注意这里Key使用了 StringRedisSerializer，那么Key只能是String类型的，不能为Long，Integer，否则会报错抛异常。（该方式主要便于排查）
	 */
	@Override
	public void putObject(Object key, Object value) {
		RedisTemplate<String,Object> redisTemplate = getStringRedisTemplate();
		//redisTemplate.setKeySerializer(new StringRedisSerializer());
		//redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.opsForHash().put(id.toString(), key.toString(), value);
		redisTemplate.expire(id.toString(),600, TimeUnit.SECONDS);//设置有效期十分钟
		
	}

	/**
	 * 缓存取值
	 */
	@Override
	public Object getObject(Object key) {
		RedisTemplate<String,Object> redisTemplate = getStringRedisTemplate();
		//redisTemplate.setKeySerializer(new StringRedisSerializer());
		//redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		return redisTemplate.opsForHash().get(id.toString(), key.toString());
	}

	/**
	 * 清空指定id缓存
	 * 没弄清楚什么时候会触发，貌似是保留方法
	 */
	@Override
	public Object removeObject(Object key) {
		return null;
	}

	/**
	 * 清空所有缓存
	 * 当有插入，修改，删除的时候会进入该方法
	 */
	@Override
	public void clear() {
		RedisTemplate<String,Object> redisTemplate = getStringRedisTemplate();
		//redisTemplate.setKeySerializer(new StringRedisSerializer());
		//redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.delete(id.toString());
	}

	/**
	 * 缓存命中率
	 */
	@Override
	public int getSize() {
		return 0;
	}

	/**
	 * 主要是处理高并发下读写出现的数据不一致问题
	 */
	@Override
	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

}
