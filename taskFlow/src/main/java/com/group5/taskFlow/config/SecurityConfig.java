package com.group5.taskFlow.config;

import com.group5.taskFlow.repository.UserRepository;
import com.group5.taskFlow.security.JwtTokenFilter;
import com.group5.taskFlow.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final HandlerExceptionResolver handlerExceptionResolver;
  private final UserRepository userRepository;

  public SecurityConfig(JwtTokenProvider jwtTokenProvider, HandlerExceptionResolver handlerExceptionResolver, UserRepository userRepository) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.handlerExceptionResolver = handlerExceptionResolver;
    this.userRepository = userRepository;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> userRepository.findByEmail(username)
            .map(user -> User.builder()
                    .username(user.getEmail())
                    .password(user.getPasswordHash())
                    .roles(user.getRoles().toArray(new String[0]))
                    .build())
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public JwtTokenFilter jwtTokenFilter(UserDetailsService userDetailsService) {
    return new JwtTokenFilter(jwtTokenProvider, userDetailsService, handlerExceptionResolver);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenFilter jwtTokenFilter) throws Exception {
    http
            .cors(Customizer.withDefaults()) // Enable CORS
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/api/auth/**", "/api/health", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**", "/swagger").permitAll()
                    .anyRequest().authenticated()
            )
            .headers(headers -> headers
                    .addHeaderWriter(new XFrameOptionsHeaderWriter(
                            XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN
                    ))
            );

    http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
