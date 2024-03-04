package com.taskmanagementsystem.tms.service.impl;

import com.taskmanagementsystem.tms.constant.Role;
import com.taskmanagementsystem.tms.models.UserInfo;
import com.taskmanagementsystem.tms.repository.UserRepository;
import com.taskmanagementsystem.tms.service.UserService;
import com.taskmanagementsystem.tms.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final JwtUtil jwtTokenProvider;
  private final MyUserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  @Override
  public UserInfo register(UserInfo user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  @Override
  public String login(UserInfo loginRequest) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(), loginRequest.getPassword()));
    final UserDetails userDetails =
        userDetailsService.loadUserByUsername(loginRequest.getUsername());

    return jwtTokenProvider.generateToken(userDetails);
  }
}
