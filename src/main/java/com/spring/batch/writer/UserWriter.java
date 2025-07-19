package com.spring.batch.writer;

import com.spring.batch.model.UserModel;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UserWriter {

    @Bean
    public JpaItemWriter<UserModel> writer(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<UserModel> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}