package nyist.com.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import nyist.com.project.entity.SysUserEntity;
import tk.mybatis.mapper.common.Mapper;

//@CacheNamespace
//@CacheNamespace(implementation = MyRedisCache.class)
//@CacheNamespaceRef(UserMapper.class)
//@CacheNamespaceRef(value = UserMapper.class)
//@CacheNamespaceRef(name="nyist.com.project.mapper.UserMapper")
public interface SysUserMapper extends Mapper<SysUserEntity>{

	@Select(" select * from sys_user where account = #{username} and password=#{password} ")
	SysUserEntity findByUserNameAndPwd(@Param("username")String username,@Param("password")String password);

	@Select(" select * from sys_user where account = #{username} ")
	SysUserEntity findByUserName(@Param("username")String username);

	@Select(" select * from sys_user where openid = #{openid} ")
	SysUserEntity findByOpenid(@Param("openid")Object openid);

	@Select(" select * from sys_user where account !='ljw' ")
	List<SysUserEntity> selectAllUser();

	
	//缓存测试 start
	List<SysUserEntity> cacheTest();

	List<SysUserEntity> cacheTest1();

	//不会被缓存
	@Select(" select u.account,r.name as realName from sys_user u left join sys_role r on u.id = r.id where u.id =1 ")
	@Options(useCache=false)
	SysUserEntity getUserRole();

	@Update(" update sys_user set create_time=now() where id=1 ")
	void updateTest1();
	
	//缓存测试 end

}
