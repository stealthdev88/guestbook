/*
 * Copyright 2014-2019 the original author or authors.
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
package guestbook;

import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The core class to bootstrap our application. It trigger the auto-configuration of the Spring Container (see
 * {@link EnableAutoConfiguration}) and activates component-scanning (see {@link ComponentScan}). At the same time the
 * class acts as configuration class to configure additional components (see {@link #init(GuestbookRepository)}) that
 * the container will take into account when bootstrapping.
 *
 * @author Paul Henke
 * @author Oliver Gierke
 */
@SpringBootApplication
public class Application {

	/**
	 * The main application method, bootstraps the Spring container.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Some initializing code to pre-fill our database with {@link GuestbookEntry}. Beans of type
	 * {@link CommandLineRunner} will be executed on application startup which makes them a convenient way to run
	 * initialization code.
	 */
	@Bean
	CommandLineRunner init(GuestbookRepository guestbook) {

		return args -> {

			Stream.of( //
					new GuestbookEntry("H4xx0r", "first!!!"), //
					new GuestbookEntry("Arni", "Hasta la vista, baby"), //
					new GuestbookEntry("Duke Nukem", "It's time to kick ass and chew bubble gum. And I'm all out of gum."), //
					new GuestbookEntry("Gump1337",
							"Mama always said life was like a box of chocolates. You never know what you're gonna get.")) //
					.forEach(guestbook::save);
		};
	}

	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	static class SecurityConfiguration extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

		/*
		 * (non-Javadoc)
		 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry)
		 */
		@Override
		public void addViewControllers(ViewControllerRegistry registry) {

			// Route requests to /login to the login view (a default one provided by Spring Security)
			registry.addViewController("/login").setViewName("login");
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
		 */
		@Override
		protected void configure(HttpSecurity http) throws Exception {

			http.csrf().disable();

			// Allow all requests on the URI level, configure form login.
			http.authorizeRequests().anyRequest().permitAll() //
					.and().formLogin() //
					.and().logout().logoutSuccessUrl("/").clearAuthentication(true);
		}
	}
}
