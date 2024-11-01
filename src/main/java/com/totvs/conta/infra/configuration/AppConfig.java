package com.totvs.conta.infra.configuration;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(value = "com.totvs.conta.infra.persistence.repository.jpa")
@ComponentScan(basePackages = {"com.totvs.conta.application.service.impl", "com.totvs.conta.infra.reader"})
@EntityScan(value = "com.totvs.conta.domain.model")
@EnableAutoConfiguration(exclude = { QuartzAutoConfiguration.class })
public class AppConfig {

}
