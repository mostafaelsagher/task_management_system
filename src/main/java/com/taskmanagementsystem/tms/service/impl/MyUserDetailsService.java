package com.taskmanagementsystem.tms.service.impl;

import com.taskmanagementsystem.tms.models.UserInfo;
import com.taskmanagementsystem.tms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) {
    UserInfo userInfo = userRepository.findByUsername(username);
    userRepository.findByUsername(username);

    if (userInfo == null) {
      throw new UsernameNotFoundException(username);
    }
    return new MyUserPrincipal(userInfo);
  }
}
