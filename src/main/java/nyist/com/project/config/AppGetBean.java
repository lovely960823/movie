package nyist.com.project.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取bean的实例对象
 * @author ljw 2020年11月2日14:35:34
 *
 */
@Component
public class AppGetBean implements ApplicationContextAware  {

	private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	public static Object getBean(String beanName){
		return applicationContext.getBean(beanName);
	}

}
