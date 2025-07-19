package com.spring.batch.processor;

import com.spring.batch.model.UserModel;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class UserProcessor implements ItemProcessor<UserModel,UserModel> {
    @Override
    public UserModel process(UserModel user) throws Exception {
        return user;
    }
}
