package nyist.com.project.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2//启动swagger
public class Swagger2Config {

		@Bean
	    public Docket createRestApi() {
	        return new Docket(DocumentationType.SWAGGER_2)
	                .apiInfo(apiInfo())
	                .select()
	                .apis(RequestHandlerSelectors.basePackage("nyist.com.project.controller"))
	                .paths(PathSelectors.any())
	                .build()
	                //security使用的
	                .securityContexts(securityContexts())
	                .securitySchemes(securitySchemes());
	    }
	    private ApiInfo apiInfo() {
	        return new ApiInfoBuilder()
	                .title("简单的接口管理说明")
	                .description("add by ljw")
	                .termsOfServiceUrl("")
	                .version("1.0")
	                .build();
	    }
	    
	  //security使用的
	    private List<SecurityContext> securityContexts(){
	    	List<SecurityContext> result = new ArrayList<>();
	    	result.add(getContextByPath("/hello/.*"));
	    	return result;
	    }
	    private SecurityContext getContextByPath(String pathRegex){
	    	return SecurityContext.builder()
	    			.securityReferences(defaultAuth())
	    			.forPaths(PathSelectors.regex(pathRegex))
	    			.build();
	    }
	    
	    private List<SecurityReference> defaultAuth(){
	    	List<SecurityReference> result = new ArrayList<>();
	    	AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
	    	AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
	    	authorizationScopes[0] = authorizationScope;
	    	result.add(new SecurityReference("Authorization", authorizationScopes));
	    	return result;
	    }
	    
	    
	    private List<ApiKey> securitySchemes(){
	    	List<ApiKey> result = new ArrayList<ApiKey>();
	    	ApiKey apiKey = new ApiKey("Authorization", "Authorization", "Header");
	    	result.add(apiKey);
	    	return result;
	    }
}
