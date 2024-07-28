package com.online.shopping.configuration;

import com.online.shopping.service.CustomSuccessHandler;
import com.online.shopping.service.CustomUserdetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class AppConfiguration {

    @Autowired
    private CustomSuccessHandler customSuccessHandler;

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserdetailService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    private static final String[] WHITE_LIST = {
            "online_shopping/user/login", "online_shopping/user/registerhere",
            "online_shopping/user/forgotpassword",
            "/online_shopping/user/addUser",
            "/online_shopping/user/verify_registrationToken",
            "/online_shopping/user/resendVerificationToken",
            "/online_shopping/user/passwordReset_request",
            "/online_shopping/user/password_reset","/online_shopping/user/changepassword",
            "/online_shopping/home/page"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request->request
                        .requestMatchers("/online_shopping/home/**",
                                "/online_shopping/user/login",
                                "/online_shopping/user/registerhere",
                                "/online_shopping/user/addUser",
                                "/online_shopping/user/verify_registrationToken",
                                "/online_shopping/user/validate/forgotpassword",
                                "/online_shopping/user/forgotpassword",
                                "/online_shopping/user/resetpassword",
                                "/Images/**")
                        .permitAll()
                        .requestMatchers("/online_shopping/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/online_shopping/user/**").hasAuthority("USER")
                        .anyRequest().authenticated())
                .formLogin(form->form.loginPage("/online_shopping/user/login")
                        .usernameParameter("email")
                        .loginProcessingUrl("/online_shopping/user/login")
                        .successHandler(customSuccessHandler).permitAll())
                .logout(form->form.invalidateHttpSession(true).clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/online_shopping/user/logout"))
                        .logoutSuccessUrl("/online_shopping/user/login?logout").permitAll());
        return http.build();
    }

}
