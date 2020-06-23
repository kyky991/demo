package com.zing.quartz.demo;

import org.quartz.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HelloJob implements Job {

    private String message;
    private Float message1;
    private Double message2;

    public String getMessage() {
        return message;
    }

    public Float getMessage1() {
        return message1;
    }

    public Double getMessage2() {
        return message2;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessage1(Float message1) {
        this.message1 = message1;
    }

    public void setMessage2(Double message2) {
        this.message2 = message2;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("current datetime:" + sf.format(date));

        System.out.println("hello");

        System.out.println("message:" + message);
        System.out.println("message1:" + message1);
        System.out.println("message2:" + message2);

        JobDetail jobDetail = context.getJobDetail();
        Trigger trigger = context.getTrigger();

        JobKey key = jobDetail.getKey();
        System.out.println(key.getName() + ":" + key.getGroup());

        TriggerKey triggerKey = trigger.getKey();
        System.out.println(triggerKey.getName() + ":" + triggerKey.getGroup());

        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        System.out.println(jobDataMap);

        JobDataMap jobDataMap1 = trigger.getJobDataMap();
        System.out.println(jobDataMap1);

        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        System.out.println(mergedJobDataMap);

        System.out.println(trigger.getStartTime());
        System.out.println(trigger.getEndTime());
    }
}
