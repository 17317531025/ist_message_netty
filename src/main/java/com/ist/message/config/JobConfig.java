package com.ist.message.config;

import com.ist.message.job.TestJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfig {

	@Value("${job.run}")
    private String jobIsRun;
	@Value("${job.cron}")
	private String jobCron;
	@Bean
    public JobDetail npSyncDataDetail() {
        return JobBuilder.newJob(TestJob.class).withIdentity("test job").storeDurably().build();
    }
	
	@Bean
    public CronTrigger npSyncDataByCronTrigger(){
		if("1".equals(jobIsRun)) {
			// Cron类型：通过cron表达式设置触发规则
			CronScheduleBuilder csb = CronScheduleBuilder.cronSchedule(String.format(jobCron))
					.withMisfireHandlingInstructionDoNothing();
			
			// 一个Trigger只对应一个Job，Schedule调度器调度Trigger执行对应的Job
			CronTrigger cTrigger = TriggerBuilder.newTrigger().forJob(npSyncDataDetail()).
					withIdentity("testJobTrigger").withDescription("corn类型的触发器").withSchedule(csb).startNow().build();
			return cTrigger;
		}
		return null;
    }
}
