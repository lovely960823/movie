package nyist.com.project.config;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * MVC全局特性配置
 * @author ljw
 *
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer{
	
	@Value("${myUploadPath}")
	private String myUploadPath;

	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		/**
		 * 虚拟路径，除了制定的去static下面的去查找文件，其他的都指向该路径下位置
		 */
		registry.addResourceHandler("/**").addResourceLocations("file:"+myUploadPath);
		
		/**
		 * 处理swagger404
		 */
		//registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
	    registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
		
	}
	
	/**
	 * 拦截器
	 */
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new MyInteceptor()).addPathPatterns("/back/**");
	}
	
	@Bean    
	public RestTemplate getRestTemplate(){        
		RestTemplate restTemplate = new RestTemplate();      
		StringHttpMessageConverter converter = new StringHttpMessageConverter();        
		converter.setDefaultCharset(Charset.forName("utf-8"));
		restTemplate.getMessageConverters().set(1,converter);        
		return restTemplate;    
	}
}
