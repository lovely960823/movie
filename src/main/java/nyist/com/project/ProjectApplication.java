package nyist.com.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("nyist.com.project.mapper")
@EnableAsync  //开启异步注解
@EnableScheduling  //开启定时任务
@EnableRedisHttpSession //开启session共享
public class ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

}
