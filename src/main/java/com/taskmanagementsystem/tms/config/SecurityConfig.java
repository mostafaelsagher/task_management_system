package com.taskmanagementsystem.tms.config;

import com.taskmanagementsystem.tms.filters.JwtRequestFilter;
import com.taskmanagementsystem.tms.service.impl.MyUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatchers;

@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

  private final MyUserDetailsService myUserDetailsService;

  /**
   * Configures the security for unsecured endpoints.
   *
   * @param http the HTTP security builder
   * @param authenticationProvider the authentication provider
   * @return the security filter chain
   * @throws Exception if an error occurs
   */
  @Bean
  @Order(1)
  public SecurityFilterChain nonSecuredFilterChain(
      HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
    String[] matchers =
        new String[] {
          "/swagger-ui/**",
          "/v3/api-docs/**",
          "/checkHealth",
          "/users/login",
          "/users/register",
          "/users/register/**",
          "/users/**",
          "/error",
          "/error/**"
        };
    http.securityMatcher(matchers);
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(request -> request.requestMatchers(matchers).permitAll())
        .sessionManagement(
            manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider);
    return http.build();
  }

  /**
   * Configures the security for secured endpoints.
   *
   * @param http the HTTP security builder
   * @param authenticationProvider the authentication provider
   * @return the security filter chain
   * @throws Exception if an error occurs
   */
  @Bean
  @Order(2)
  public SecurityFilterChain securedFilterChain(
      HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {

    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(request -> request.anyRequest().authenticated())
        .exceptionHandling(
            c ->
                c.accessDeniedHandler(
                    (request, response, accessDeniedException) ->
                        response.sendError(HttpServletResponse.SC_FORBIDDEN)))
        .sessionManagement(
            manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(new JwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  /**
   * Configures the authentication provider for the application.
   *
   * @param passwordEncoder the password encoder
   * @return the authentication provider
   */
  @Bean
  public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder);
    authProvider.setUserDetailsService(myUserDetailsService);
    return authProvider;
  }

  /**
   * Configures the {@link PasswordEncoder} for the application
   * @return the {@link PasswordEncoder}
   */

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  /**
   * Configures the {@link AuthenticationManager} for the application
   * @return the {@link AuthenticationManager}
   * @throws Exception if an error occurs
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}
