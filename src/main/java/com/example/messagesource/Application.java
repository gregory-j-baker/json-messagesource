/**
 * Copyright (c) 2022 Her Majesty the Queen in Right of Canada, as represented by the Employment and Social Development Canada. All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE.md file in the root directory of this source tree.
 */

package com.example.messagesource;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

import com.example.messagesource.context.support.JsonMessageSource;

/**
 * @author Greg Baker (gregory.j.baker@hrsdc-rhdcc.gc.ca)
 * @since 0.0.0
 */
@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@ConfigurationProperties("spring.messages")
	@Bean MessageSource messageSource() {
		log.info("Creating 'messageSource' bean");
		return new JsonMessageSource();
	}

	@Bean ApplicationRunner applicationRunner() {
		final MessageSource messageSource = messageSource();

		return args -> {
			final String messageEn = messageSource.getMessage("mxessage", null, Locale.ENGLISH);
			final String messageFr = messageSource.getMessage("message", null, Locale.FRENCH);

			log.info("English message from JSON message bundle: [{}]", messageEn);
			log.info("French message from JSON message bundle: [{}]", messageFr);
		};
	}

}
