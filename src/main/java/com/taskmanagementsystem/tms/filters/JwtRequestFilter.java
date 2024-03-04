package com.taskmanagementsystem.tms.filters;

import com.taskmanagementsystem.tms.exception.JWTAuthenticationEntryPoint;
import com.taskmanagementsystem.tms.service.impl.MyUserDetailsService;
import com.taskmanagementsystem.tms.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT filter implementation that should not be created as a bean to control which requests get the
 * filter applied. see {@link com.taskmanagementsystem.tms.config.SecurityConfig}
 */
public class JwtRequestFilter extends OncePerRequestFilter {

  private MyUserDetailsService userDetailsService;
  private JwtUtil jwtUtil;
  private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain chain)
      throws ServletException, IOException {

    try {

      // Dependencies
      if (userDetailsService == null) {
        WebApplicationContext ctx =
            WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
        if (ctx != null) {
          userDetailsService = ctx.getBean(MyUserDetailsService.class);
          jwtUtil = ctx.getBean(JwtUtil.class);
          jwtAuthenticationEntryPoint = ctx.getBean(JWTAuthenticationEntryPoint.class);
        }
      }

      final String authorizationHeader = request.getHeader("Authorization");

      String username;
      String jwt;

      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        jwt = authorizationHeader.substring(7);
        username = jwtUtil.extractUsername(jwt);
      } else {
        // 401
        throw new InsufficientAuthenticationException("Invalid authorization header");
      }

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(jwt, userDetails)) {

          UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          usernamePasswordAuthenticationToken.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
      }
    } catch (AuthenticationException ex) {
      this.jwtAuthenticationEntryPoint.commence(request, response, ex);
      return;
    }

    chain.doFilter(request, response);
  }
}
