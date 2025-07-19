package com.spring.batch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class UserController {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job userJob;

    @GetMapping("/startJob")
    public String startJob() throws Throwable{
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time",System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(userJob,jobParameters);
        return "Job status: "+execution.getStatus();
    }

}