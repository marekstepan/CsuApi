package com.tribes.csuapi.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tribes.csuapi.model.User;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.var;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  @Value("auth.jwt.signing_key")
  private String jwtSigningKey;

  private UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(RequestMatcher requestMatcher,
                                 AuthenticationManager authenticationManager,
                                 UserDetailsService userDetailsService) {
    super(requestMatcher);
    setAuthenticationManager(authenticationManager);
    this.userDetailsService = userDetailsService;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
                                              HttpServletResponse response) throws
      AuthenticationException, IOException, ServletException {
    String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      throw new RuntimeException("No JWT token found in request headers");
    }

    String authToken = header.substring("Bearer ".length());
    Algorithm algorithm = Algorithm.HMAC256(jwtSigningKey);
    JWTVerifier verifier = JWT.require(algorithm)
        .build();
    DecodedJWT decodedJWT = verifier.verify(authToken);
    String username = decodedJWT.getSubject();
    User user = (User) userDetailsService.loadUserByUsername(username);
    var token =
        new UsernamePasswordAuthenticationToken(user.getUsername(), "", user.getAuthorities());
    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    return token;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                          FilterChain chain, Authentication authResult)
      throws IOException, ServletException {
    SecurityContextHolder.getContext().setAuthentication(authResult);
    chain.doFilter(request, response);
  }
}
