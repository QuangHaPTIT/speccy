package com.speccy.speccy.infrastructure.configuration.i18n;

import com.speccy.speccy.application.constants.GlobalConstants;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class MessageConfig {

    @Bean
    public MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:/messages/Messages");
        messageSource.setDefaultEncoding(GlobalConstants.DEFAULT_ENCODING);
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setDefaultLocale(GlobalConstants.ENGLISH_LOCALE);

        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean getValidator() {
        final LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }
}
