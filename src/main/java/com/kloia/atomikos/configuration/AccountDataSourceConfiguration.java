package com.kloia.atomikos.configuration;

import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "AccountDataSourceConfiguration",
        transactionManagerRef = "transactionManager",
        basePackages = {"com.kloia.atomikos.repository.account"}
)
public class AccountDataSourceConfiguration {

    private final JpaProperties jpaProperties;

    public AccountDataSourceConfiguration(JpaProperties jpaProperties) {
        this.jpaProperties = jpaProperties;
    }

    @Bean(name = "accountEntityManagerFactoryBuilder")
//    @Primary
    public EntityManagerFactoryBuilder accountEntityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(
                new HibernateJpaVendorAdapter(), jpaProperties.getProperties(), null
        );
    }


    @Bean(name = "AccountDataSourceConfiguration")
//    @Primary
    public LocalContainerEntityManagerFactoryBean accountEntityManager(
            @Qualifier("accountEntityManagerFactoryBuilder") EntityManagerFactoryBuilder accountEntityManagerFactoryBuilder,
            @Qualifier("accountDataSource") DataSource postgresDataSource
    ) {
        return accountEntityManagerFactoryBuilder
                .dataSource(postgresDataSource)
                .packages("com.kloia.atomikos.model.account")
                .persistenceUnit("postgres")
                .properties(jpaProperties.getProperties())
                .jta(true)
                .build();
    }

    @Bean("accountDataSourceProperties")
//    @Primary
    @ConfigurationProperties("datasource.account")
    public DataSourceProperties accountDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean("accountDataSource")
//    @Primary
    @ConfigurationProperties("datasource.account")
    public DataSource accountDataSource(@Qualifier("accountDataSourceProperties") DataSourceProperties accountDataSourceProperties) {
        PGXADataSource ds = new PGXADataSource();
        ds.setUrl(accountDataSourceProperties.getUrl());
        ds.setUser(accountDataSourceProperties.getUsername());
        ds.setPassword(accountDataSourceProperties.getPassword());

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(ds);
        xaDataSource.setUniqueResourceName("xa_account");
        return xaDataSource;
    }

    /*
    @Bean(name = "accountTransactionManager")
    public JpaTransactionManager transactionManager(@Qualifier("accountEntityManager") EntityManagerFactory accountEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(accountEntityManager);
        return transactionManager;
    }
    */

}