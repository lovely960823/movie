package nyist.com.project.shiro;

import org.springframework.context.annotation.Configuration;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
/**
 * shiro 配置类
 * @author ljw 2020年11月2日15:09:08
 *
 */
@Configuration
public class ShiroConfig {


 	@Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager")DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //设置安全管理器
        bean.setSecurityManager(defaultWebSecurityManager);

        //添加shiro的内置过滤器
        /*
            anon: 无需认证就可访问
            authc：必须认证才能访问
            user：必须拥有记住我功能才能访问
            perms: 拥有对某个资源的权限才能访问
            role:拥有某个角色权限才能访问
       */

        // 配置访问权限 必须是LinkedHashMap，因为它必须保证有序
        LinkedHashMap<String, String> filterMap = new LinkedHashMap<>();
        //授权
        filterMap.put("/static/**", "anon");//静态资源不拦截
        //设置退出
        filterMap.put("/logout", "logout");
        //设置登录请求
        bean.setLoginUrl("/toLogin");
        bean.setFilterChainDefinitionMap(filterMap);
        return bean;
    }
	
    @Bean(name="securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("myRealm") MyRealm myRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myRealm);
        securityManager.setCacheManager(cacheManager());
        return securityManager;
    }
	
    /**
     * 自定义realm
     * @return
     */
	@Bean(name = "myRealm")
	public MyRealm myRealm(){
		return new MyRealm();
	}
	
	/**
	 * html使用shiro标签配置，我的jsp页面貌似不需要，  页面属性加上这个：xmlns:shiro="http://www.pollix.at/thymeleaf/shiro"
	 * @return
	 */
	@Bean
	public ShiroDialect shiroDialect(){
		return new ShiroDialect();
	}

	//开启shiro aop注解支持  作用在方法上
	@Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
	
	/**
	 * 增加权限缓存管理器
	 * @return
	 */
	@Bean(name = "cacheManager")
    public EhCacheManager cacheManager() {
        EhCacheManager cacheManager = new EhCacheManager();
        net.sf.ehcache.CacheManager cm = net.sf.ehcache.CacheManager.getCacheManager("shirocache");
        if (cm == null) {
            String configFile = "classpath:ehcache/ehcache-shiro.xml";
            InputStream is = null;
            try {
                is = ResourceUtils.getInputStreamForPath(configFile);
                cm = new net.sf.ehcache.CacheManager(is);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to obtain input stream for cacheManagerConfigFile [" + configFile + "]", e);
            } finally {
                ResourceUtils.close(is);
            }
        }
        cacheManager.setCacheManager(cm);
        return cacheManager;
    }
}
