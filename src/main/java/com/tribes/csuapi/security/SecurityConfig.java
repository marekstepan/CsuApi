package com.tribes.csuapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private MemoryBasedUserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilterBefore(jwtAuthenticationFilter(authenticationManagerBean()),
            UsernamePasswordAuthenticationFilter.class)
        .authorizeRequests()
        .requestMatchers(publicPaths()).permitAll()
        .antMatchers("/test/secured/adminEndpoint").hasRole("ADMIN")
        .antMatchers("/test/secured/**").authenticated()
        .anyRequest().authenticated()
    ;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(
      AuthenticationManager authenticationManager) {
    return new JwtAuthenticationFilter(protectedPaths(), authenticationManager, userDetailsService);
  }

  @Bean
  public RequestMatcher protectedPaths() {
    return new NegatedRequestMatcher(publicPaths());
  }

  @Bean
  public RequestMatcher publicPaths() {
    return new OrRequestMatcher(new AntPathRequestMatcher("/test/login"),
        new AntPathRequestMatcher("/test/testEndpoint/**"), new AntPathRequestMatcher("/error"));
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
}
