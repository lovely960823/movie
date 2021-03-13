package nyist.com.project.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import nyist.com.project.mapper.SysGlobalLogMapper;

@Configuration
public class ScheduleTask {
	
	@Autowired
	SysGlobalLogMapper sysGlobalLogMapper;

    @Scheduled(cron = "0 0 1 * * ?")
    public void configureTasks() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, - 7);
        Date d = c.getTime();
        String day = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(d);//获取当前时间前一周
        sysGlobalLogMapper.deleteByTime(day);
        System.out.println("当前时间前一周："+day+"");
    }
}
