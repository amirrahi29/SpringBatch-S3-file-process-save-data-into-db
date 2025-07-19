package com.spring.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class UserListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution execution){
        System.out.println("Job name: "+execution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution execution){
        System.out.println("Job status: "+execution.getStatus());
    }

}