package dnf.module.auction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/login/**", "/signup/**",  "/css/**", "/js/**", "/user/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/doLogin")
                .usernameParameter("userId")
                .passwordParameter("passwd")
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .and()
                .logout()
        ;
    }

}