package ApiRest.apiRest;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
 
@Override
  protected void configure(HttpSecurity http) throws Exception {
	http.csrf().disable();
            http.authorizeRequests().anyRequest().permitAll();
             // http.csrf().csrfTokenRepository(csrfTokenRepository());
        }
/*
private CsrfTokenRepository csrfTokenRepository() 
{ 
    HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository(); 
    repository.setSessionAttributeName("_csrf");
    return repository; 
}*/
}
