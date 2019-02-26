package com.dppware.centralizedConfigServer.config;

//@Configuration
//@EnableWebSecurity
public class WebSecurityConfiguration {}
/**extends WebSecurityConfigurerAdapter {
 
	@Value(value="${spring.security.user.name}")
	private String userName;
	@Value(value="${spring.security.user.password}")
	private String password;
	
	
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
      throws Exception {
    	System.out.println(userName + " "+ password);
    	System.out.println(userName + " "+ password);System.out.println(userName + " "+ password);System.out.println(userName + " "+ password);
    	
    	
        auth
          .inMemoryAuthentication()
          .withUser(userName)
            .password("{noop}"+password)
            .roles("USER")
            .and()
          .withUser("admin")
            .password("{noop}"+"admin")
            .roles("USER", "ADMIN");
    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
          .authorizeRequests()
          .anyRequest()
          .authenticated()
          .and()
          .httpBasic();
    }
}**/