package com.spring.batch.config;

import com.spring.batch.listener.UserListener;
import com.spring.batch.model.UserModel;
import com.spring.batch.processor.UserProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.core.launch.support.RunIdIncrementer;

@Configuration
public class BatchJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public BatchJobConfig(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Step userStep(
            MultiResourceItemReader<UserModel> reader,
            UserProcessor processor,
            JpaItemWriter<UserModel> writer
    ) {
        return new StepBuilder("userStep", jobRepository)
                .<UserModel, UserModel>chunk(new SimpleCompletionPolicy(1000), transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job userJob(
            Step userStep,
            UserListener listener
    ) {
        return new JobBuilder("userJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(userStep)
                .build();
    }
}