package api;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .httpBasic().disable()
                .csrf().disable() //enable post within credentials
                .cors();
//                .and()
//                .csrf().disable()
//                .exceptionHandling()
//                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
//                .and()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.GET, "/users/").hasAuthority("ADMIN")
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .successHandler(getRestAuthenticationSuccessHandler())
//                .failureHandler(getRestOnAuthenticationFailureHandler())
//                .and()
//                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                .deleteCookies("JSESSIONID").invalidateHttpSession(true);
    }

//    @Override
//    public void configure(WebSecurity webSecurity) throws Exception {
//        webSecurity.ignoring()
//                .antMatchers("/test/**")
//                .antMatchers(HttpMethod.OPTIONS, "/**")
//                .antMatchers(HttpMethod.GET, "/ping")
//                .antMatchers(HttpMethod.POST ,"/users/")
//                .antMatchers(HttpMethod.POST, "/users")
//                .antMatchers(HttpMethod.POST, "/users/update/**");
//    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));

        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(Arrays.asList("Authorization",
                                                        "Cache-Control",
                                                        "Content-Type",
                                                        "Access-Control-Allow-Origin",
                                                        "Access-Control-Allow-Headers",
                                                        "Access-Control-Allow-Credentials",
                                                        "withCredentials",
                                                        "X-Requested-With",
                                                        "requestId",
                                                        "Correlation-Id",
                                                        "Set-Cookie"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

//    @Override
//    protected void configure(HttpSecurity security) throws Exception
//    {
//        security.httpBasic().disable();
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
//        authManagerBuilder.authenticationProvider(customAuthenticationProvider());
//    }
//
//    @Override
//    public UserDetailsService userDetailsServiceBean(){
//        return new AMSUserDetailsService(userService, authService);
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider customAuthenticationProvider(){
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(passwordEncoder());
//        provider.setUserDetailsService(userDetailsServiceBean());
//        return provider;
//    }
//
//    @Bean
//    public RestOnLoginSuccessHandler getRestAuthenticationSuccessHandler(){
//        return new RestOnLoginSuccessHandler(userService);
//    }
//
//    @Bean
//    AuthenticationFailureHandler getRestOnAuthenticationFailureHandler(){
//        return new RestOnAuthenticationFailureHandler();
//    }
}





