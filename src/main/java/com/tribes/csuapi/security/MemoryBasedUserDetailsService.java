package com.tribes.csuapi.security;

import com.tribes.csuapi.model.Role;
import com.tribes.csuapi.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MemoryBasedUserDetailsService implements UserDetailsService {

  private PasswordEncoder passwordEncoder;

  private Map<String, User> users = new HashMap<>();

  @Autowired
  private MemoryBasedUserDetailsService(PasswordEncoder passwordEncoder) {
    Role user = new Role("ROLE_USER");
    Role admin = new Role("ROLE_ADMIN");
    users.put("paris_szechenyi", new User("paris_szechenyi", passwordEncoder.encode("MDtmh2016"),
        Stream.of(user).collect(Collectors.toSet())));
    users.put("admin", new User("admin", passwordEncoder.encode("MDtmh2016"),
        Stream.of(user, admin).collect(Collectors.toSet())));
  }


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = users.get(username);
    if (user == null) {
      throw new UsernameNotFoundException("Login failed");
    } else {
      return user;
    }
  }
}

