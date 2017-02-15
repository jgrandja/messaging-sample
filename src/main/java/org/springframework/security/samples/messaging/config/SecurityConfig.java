/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.samples.messaging.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.samples.messaging.data.UserRepository;
import org.springframework.security.samples.messaging.security.UserRepositoryUserDetailsService;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * @author Rob Winch
 * @author Joe Grandja
 */
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private UserRepository userRepository;

	@Configuration
	@Order(2)
	public static class AppSecurityConfig extends WebSecurityConfigurerAdapter {

		// @formatter:off
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.csrf()
					.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
					.and()
				.authorizeRequests()
					.antMatchers("/", "/assets/**", "/webjars/**").permitAll()
					.antMatchers("/users/{userId}").access("@authz.check(#userId,principal)")
					.anyRequest().hasRole("USER")
					.and()
				.httpBasic();
		}
		// @formatter:on

	}

	@Configuration
	@Order(1)
	public static class AdminSecurityConfig extends WebSecurityConfigurerAdapter {

		// @formatter:off
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.antMatcher("/admin/**")
				.authorizeRequests()
					.antMatchers("/assets/**", "/webjars/**").permitAll()
					.anyRequest().hasRole("ADMIN")
					.and()
				.formLogin()
					.loginPage("/admin/login")
					.failureUrl("/admin/login-error")
					.permitAll()
					.and()
				.logout()
					.logoutUrl("/admin/logout");
		}
		// @formatter:on

	}

	// @formatter:off
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userDetailsService())
				.passwordEncoder(passwordEncoder());
	}
	// @formatter:on

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserRepositoryUserDetailsService(this.userRepository);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}