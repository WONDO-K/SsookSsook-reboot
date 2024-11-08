package com.stillalive.Ssook_BE.util.scheduler;

import com.stillalive.Ssook_BE.util.scheduler.job.DailySpentResetJob;
import com.stillalive.Ssook_BE.util.scheduler.job.MonthlyBalanceExpireJob;
import com.stillalive.Ssook_BE.util.scheduler.job.MonthlyBalanceResetJob;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    private static final Logger log = LoggerFactory.getLogger(QuartzConfig.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @Bean
    public SpringBeanJobFactory jobFactory() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        factory.setDataSource(dataSource);  // Quartz가 사용할 DataSource 설정
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true);
        log.info("Quartz Scheduler Factory Bean이 초기화되었습니다.");
        return factory;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory) throws SchedulerException {
        Scheduler scheduler = factory.getScheduler();
        scheduler.scheduleJob(balanceResetJobDetail(), balanceResetTrigger());
        scheduler.scheduleJob(balanceExpireJobDetail(), balanceExpireTrigger());
        scheduler.scheduleJob(dailySpentResetJobDetail(), dailySpentResetTrigger());
        scheduler.start();
        log.info("Quartz Scheduler가 시작되었습니다.");
        return scheduler;
    }

    // 매월 1일 자정에 잔액을 갱신
    @Bean
    public JobDetail balanceResetJobDetail() {
        return JobBuilder.newJob(MonthlyBalanceResetJob.class)
                .withIdentity("balanceResetJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger balanceResetTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(balanceResetJobDetail())
                .withIdentity("balanceResetTrigger")
                .withSchedule(CronScheduleBuilder.monthlyOnDayAndHourAndMinute(1, 0, 0)) // 매월 1일 00:00
                .build();
    }

    // 매월 마지막 날 23:59:59에 잔액을 소멸
    @Bean
    public JobDetail balanceExpireJobDetail() {
        return JobBuilder.newJob(MonthlyBalanceExpireJob.class)
                .withIdentity("balanceExpireJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger balanceExpireTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(balanceExpireJobDetail())
                .withIdentity("balanceExpireTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("59 59 23 L * ?")) // 매월 마지막 날 23:59:59
//                .withSchedule(CronScheduleBuilder.cronSchedule("0 */1 * * * ?")) // 1분마다 실행
                .build();
    }

    // 매일 자정에 일일 지출 금액 초기화
    @Bean
    public JobDetail dailySpentResetJobDetail() {
        return JobBuilder.newJob(DailySpentResetJob.class)
                .withIdentity("dailySpentResetJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger dailySpentResetTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(dailySpentResetJobDetail())
                .withIdentity("dailySpentResetTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 0)) // 매일 자정 00:00
//                .withSchedule(CronScheduleBuilder.cronSchedule("0 */1 * * * ?")) // 1분마다 실행
                .build();
    }
}
