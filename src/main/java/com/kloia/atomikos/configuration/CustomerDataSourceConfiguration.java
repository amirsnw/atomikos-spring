package com.kloia.atomikos.configuration;

import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "CustomerDataSourceConfiguration",
        transactionManagerRef = "transactionManager",
        basePackages = {"com.kloia.atomikos.repository.customer"}
)
public class CustomerDataSourceConfiguration {

    private final JpaProperties jpaProperties;

    public CustomerDataSourceConfiguration(JpaProperties jpaProperties) {
        this.jpaProperties = jpaProperties;
    }

    @Bean(name = "customerEntityManagerFactoryBuilder")
    @Primary
    public EntityManagerFactoryBuilder customerEntityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(
                new HibernateJpaVendorAdapter(), jpaProperties.getProperties(), null
        );
    }


    @Bean(name = "CustomerDataSourceConfiguration")
    @Primary
    public LocalContainerEntityManagerFactoryBean getPostgresEntityManager(
            @Qualifier("customerEntityManagerFactoryBuilder") EntityManagerFactoryBuilder customerEntityManagerFactoryBuilder,
            @Qualifier("customerDataSource") DataSource postgresDataSource
    ) {
        return customerEntityManagerFactoryBuilder
                .dataSource(postgresDataSource)
                .packages("com.kloia.atomikos.model.customer")
                .persistenceUnit("postgres")
                .properties(jpaProperties.getProperties())
                .jta(true)
                .build();
    }

    @Bean("customerDataSourceProperties")
    @Primary
    @ConfigurationProperties("datasource.customer")
    public DataSourceProperties customerDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean("customerDataSource")
    @Primary
    @ConfigurationProperties("datasource.customer")
    public DataSource customerDataSource(@Qualifier("customerDataSourceProperties") DataSourceProperties customerDataSourceProperties) {
        // return customerDataSourceProperties.initializeDataSourceBuilder().build();
        PGXADataSource ds = new PGXADataSource();
        ds.setUrl(customerDataSourceProperties.getUrl());
        ds.setUser(customerDataSourceProperties.getUsername());
        ds.setPassword(customerDataSourceProperties.getPassword());

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(ds);
        xaDataSource.setUniqueResourceName("xa_customer");
        return xaDataSource;
    }

    /*
    @Bean(name = "customerTransactionManager")
    public JpaTransactionManager transactionManager(@Qualifier("customerEntityManager") EntityManagerFactory customerEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(customerEntityManager);
        return transactionManager;
    }
    */

}