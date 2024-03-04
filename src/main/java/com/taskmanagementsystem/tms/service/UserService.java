package com.taskmanagementsystem.tms.service;

import com.taskmanagementsystem.tms.models.UserInfo;
import org.springframework.http.ResponseEntity;

public interface UserService {
    UserInfo register(UserInfo user);
    String login(UserInfo loginRequest);
}
