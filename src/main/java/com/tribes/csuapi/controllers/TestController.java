package com.tribes.csuapi.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tribes.csuapi.exceptions.CsuApiException;
import com.tribes.csuapi.integration.CsuApiService;
import com.tribes.csuapi.integration.model.Data;
import com.tribes.csuapi.model.User;
import com.tribes.csuapi.security.MemoryBasedUserDetailsService;
import java.io.IOException;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RestController
@RequestMapping("/test")
public class TestController {


  @Value("auth.jwt.signing_key")
  private String jwtSigningKey;

  @Autowired
  private MemoryBasedUserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @GetMapping("/login")
  public String login(@RequestParam String username, @RequestParam String password) {
    try {
      User user = (User) userDetailsService.loadUserByUsername(username);
      if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new RuntimeException("Invalid login");
      }
      Algorithm algorithm = Algorithm.HMAC256(jwtSigningKey);
      String token = JWT.create()
          .withSubject(username)
          .sign(algorithm);
      return token;
    } catch (UsernameNotFoundException e) {
      throw new RuntimeException("Invalid login");
    }
  }

  @GetMapping("/testEndpoint")
  public String doSomething() {
    return "aaa";
  }

  @GetMapping(value = "/secured/securedEndpoint", produces = "application/json")
  public Data doSomethingSecure() throws CsuApiException {
    Retrofit retrofit2 = new Retrofit.Builder()
        .baseUrl("https://api.apitalks.store/czso.cz/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    CsuApiService service2 = retrofit2.create(CsuApiService.class);
    try {
      Data data =
          service2.listMunicipalities("{\"where\": {\"vuzemi_kod\":592528}}").execute().body();
      return data;
    } catch (IOException e) {
      throw new CsuApiException(e);
    }
  }

  @GetMapping("/secured/userProfile")
  public Principal getUserProfile(Principal principal) {
    return principal;
  }

  @GetMapping("/secured/adminEndpoint")
  public String doSomethingWithAdmin() {
    return "admin";
  }
}
