package com.kloia.atomikos.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JpaProperties {

    private final Environment environment;

    public JpaProperties(Environment environment) {
        this.environment = environment;
    }

    public Map<String, String> getProperties() {
        Map<String, String> propertiesMap = new HashMap<>();

        propertiesMap.put("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
        propertiesMap.put("hibernate.dialect", environment.getProperty("hibernate.dialect"));
        propertiesMap.put("hibernate.show_sql", environment.getProperty("hibernate.show_sql"));
        propertiesMap.put("hibernate.temp.use_jdbc_metadata_defaults", environment.getProperty("hibernate.temp.use_jdbc_metadata_defaults"));
        propertiesMap.put("hibernate.transaction.jta.platform", environment.getProperty("hibernate.transaction.jta.platform"));
        propertiesMap.put("javax.persistence.transactionType", environment.getProperty("javax.persistence.transactionType"));

        return propertiesMap;
    }
}
