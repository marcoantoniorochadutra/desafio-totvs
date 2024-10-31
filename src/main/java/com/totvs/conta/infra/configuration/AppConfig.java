package com.totvs.conta.infra.configuration;


import com.github.dozermapper.spring.DozerBeanMapperFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

@Configuration
@EnableJpaRepositories(value = "com.totvs.conta.infra.persistence.repository.jpa")
@ComponentScan(basePackages = {"com.totvs.conta.application.service.impl", "com.totvs.conta.infra.reader"})
@EntityScan(value = "com.totvs.conta.domain.model")
@EnableAutoConfiguration(exclude = { QuartzAutoConfiguration.class })
public class AppConfig {

    @Bean
    public DozerBeanMapperFactoryBean dozerMapper(ResourcePatternResolver resourcePatternResolver) throws IOException {
        DozerBeanMapperFactoryBean factoryBean = new DozerBeanMapperFactoryBean();
        factoryBean.setMappingFiles(resourcePatternResolver.getResources("classpath*:/*mapping.xml"));
        return factoryBean;
    }
}
