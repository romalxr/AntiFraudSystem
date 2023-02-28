package antifraud.security;

import static antifraud.entity.Role.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    UserDetailsService userDetailsService;
    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(getEntryPoint()) // Handles auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests() // manage access
                .mvcMatchers("/actuator/shutdown")
                    .permitAll() // needs to run test
                .mvcMatchers(HttpMethod.POST,"/api/auth/user")
                    .permitAll()
                .mvcMatchers("/api/auth/list")
                    .hasAnyAuthority(ADMINISTRATOR.name(), SUPPORT.name())
                .mvcMatchers(HttpMethod.POST,"/api/antifraud/transaction")
                    .hasAuthority(MERCHANT.name())
                .mvcMatchers("/api/auth/**")
                    .hasAuthority(ADMINISTRATOR.name())
                .mvcMatchers("/api/antifraud/suspicious-ip/**")
                    .hasAuthority(SUPPORT.name())
                .mvcMatchers("/api/antifraud/stolencard/**")
                    .hasAuthority(SUPPORT.name())
                .mvcMatchers("/api/antifraud/history/**")
                    .hasAuthority(SUPPORT.name())
                .mvcMatchers(HttpMethod.PUT,"/api/antifraud/transaction")
                    .hasAuthority(SUPPORT.name())
                .mvcMatchers("/**").authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }
    @Bean
    public AuthenticationEntryPoint getEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService) // user store 1
                .passwordEncoder(getEncoder());
    }
    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
