package com.hrm.project.user_service.config;


import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig {

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver slr = new AcceptHeaderLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }

    @Bean
    public MessageSource messageSource() {
        // You instantiate the concrete class here...
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("messages/message");
        source.setUseCodeAsDefaultMessage(true);
        source.setDefaultEncoding("UTF-8");// Returns the key if the message is missing
        return source; // ...but you return it AS a MessageSource interface
    }

}
