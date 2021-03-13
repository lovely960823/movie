package nyist.com.project.mapper;

import org.apache.ibatis.annotations.CacheNamespace;

import nyist.com.project.cache.MyRedisCache;
import nyist.com.project.entity.ExceptionLogEntity;
import tk.mybatis.mapper.common.Mapper;

@CacheNamespace(implementation = MyRedisCache.class)
public interface ExceptionLogMapper extends Mapper<ExceptionLogEntity>{

}
