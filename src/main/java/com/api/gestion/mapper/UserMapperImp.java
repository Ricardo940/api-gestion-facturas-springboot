package com.api.gestion.mapper;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.api.gestion.pojo.User;
import com.api.gestion.wrapper.UserWrapper;

@Service
public class UserMapperImp {
    
     public UserWrapper mapearDeUser(User user){
        UserWrapper userWrapper = new UserWrapper();
        BeanUtils.copyProperties(user, userWrapper);
        return userWrapper;
    }

}
