package nyist.com.project.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component 
@ConfigurationProperties(prefix = "ly.pay.wx")
@Data
public class PayProperties {
	
	 private String appID;    
	 
	 private String mchID;  
	 
	 private String key;  
	 
	 private String notifyurl;

}
