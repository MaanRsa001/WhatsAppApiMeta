package com.maan.whatsapp.auth.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class ApplicationConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private BasicAuthenticationPoint basicAuthenticationPoint;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();
		http.authorizeRequests().antMatchers("/")/* .hasAnyRole("USER") */.fullyAuthenticated().and().httpBasic();
		http.authorizeRequests().antMatchers("/", "/api/**","/whatsapp/logo/**").permitAll().anyRequest().authenticated().and().httpBasic();
		http.cors();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.httpBasic().authenticationEntryPoint(basicAuthenticationPoint);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("whatsappchatapi")
				.password(passwordEncoder().encode("whatsappchatapi@123#"))
				.roles("USER");
	}

	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/", "/resources/**", "/styles/**", "/static/**", "/jasper/**", "/public/**",
				"/webui/**", "/h2-console/**", "/*.jsp", "/**/*.jsp", "/configuration/**", "/swagger-ui/**", "/ui/**",
				"/swagger-resources/**", "/api-docs", "/api-docs/**", "/fonts/**", "/v2/api-docs/**", "/*.html",
				"/**/*.html", "/*.jpg", "/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.gif", "/**/*.svg",
				"/**/*.ico", "/**/*.ttf", "/**/*.woff", "/**/*.woff2", "/**/*.otf", "/whatsapp/webhook","/whatsapp/webhook/meta/**","/whatsapptemplate/document/download/**",
"/insurance/buypolicy/**");
	}

}