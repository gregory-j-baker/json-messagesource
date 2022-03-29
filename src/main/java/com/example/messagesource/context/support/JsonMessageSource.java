/**
 * Copyright (c) 2022 Her Majesty the Queen in Right of Canada, as represented by the Employment and Social Development Canada. All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE.md file in the root directory of this source tree.
 */

package com.example.messagesource.context.support;

import static org.springframework.util.StringUtils.commaDelimitedListToStringArray;
import static org.springframework.util.StringUtils.trimAllWhitespace;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Greg Baker (gregory.j.baker@hrsdc-rhdcc.gc.ca)
 * @since 0.0.0
 */
public class JsonMessageSource extends AbstractMessageSource {

	private static final Logger log = LoggerFactory.getLogger(JsonMessageSource.class);

	private static final String FILENAME_SUFFIX = ".json";

	private Set<String> basenames = Collections.emptySet();

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final ResourceLoader resourceLoader = new DefaultResourceLoader();

	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		for (final String basename : basenames) {
			for (final String filename : getFilenamesForLocale(basename, locale)) {
				final String message = getProperties(filename).getProperty(code);
				if (message != null) { return createMessageFormat(message, locale); }
			}
		}

		return null;
	}

	/*
	 * Method copied from ReloadableResourceBundleMessageSource.calculateFilenamesForLocale(String, Locale)
	 */
	protected Set<String> getFilenamesForLocale(String basename, Locale locale) {
		final Set<String> filenames = new HashSet<>();

		final String language = locale.getLanguage();
		final String country = locale.getCountry();
		final String variant = locale.getVariant();

		final StringBuilder stringBuilder = new StringBuilder(basename);

		stringBuilder.append('_');
		if (language.length() > 0) {
			stringBuilder.append(language);
			filenames.add(stringBuilder.toString());
		}

		stringBuilder.append('_');
		if (country.length() > 0) {
			stringBuilder.append(country);
			filenames.add(stringBuilder.toString());
		}

		if (variant.length() > 0 && (language.length() > 0 || country.length() > 0)) {
			stringBuilder.append('_').append(variant);
			filenames.add(stringBuilder.toString());
		}

		return filenames;
	}

	protected Properties getProperties(String filename) {
		final Resource resource = resourceLoader.getResource(filename + FILENAME_SUFFIX);

		if (resource.exists()) {
			try {
				return objectMapper.readValue(resource.getInputStream(), Properties.class);
			}
			catch (final IOException exception) {
				log.warn("Could not read resources with filename [{}]", filename, exception);
			}
		}

		return new Properties();
	}

	public void setBasename(String basename) {
		setBasenames(commaDelimitedListToStringArray(trimAllWhitespace(basename)));
	}

	public void setBasenames(String ... basenames) {
		this.basenames = Stream.of(basenames).collect(Collectors.toSet());
	}

}
