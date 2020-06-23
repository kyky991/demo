package com.zing.quartz.demo;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class HelloScheduler {

    public static void main(String[] args) throws SchedulerException, InterruptedException {
        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class)
                .withIdentity("myJob", "group1")
                .usingJobData("message", "job1")
                .usingJobData("message1", 3.14f)
                .build();

        System.out.println(jobDetail.getKey().getName());
        System.out.println(jobDetail.getKey().getGroup());
        System.out.println(jobDetail.getJobClass().getName());

        Date date = new Date();
        date.setTime(date.getTime() + 3000);

        Date end = new Date();
        end.setTime(end.getTime() + 6000);

//        Trigger trigger = TriggerBuilder
//                .newTrigger()
//                .withIdentity("myTrigger", "group1")
//                .usingJobData("message", "trigger1")
//                .usingJobData("message2", 3.1415926)
//                .startAt(date)
//                .endAt(end)
//                .withSchedule(
//                        SimpleScheduleBuilder.simpleSchedule()
//                                .withIntervalInSeconds(2).repeatForever())
//                .build();

        CronTrigger trigger = (CronTrigger) TriggerBuilder
                .newTrigger()
                .withIdentity("myTrigger", "group1")
                .usingJobData("message", "trigger1")
                .usingJobData("message2", 3.1415926)
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("* * * * * ? *"))
                .build();

        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();

        System.out.println("scheduler time:" + scheduler.scheduleJob(jobDetail, trigger));

        Thread.sleep(2000L);
        scheduler.standby();

        Thread.sleep(3000L);
        scheduler.start();

        Thread.sleep(2000L);
        scheduler.shutdown();//shutdown(false)
    }
}
