package com.bootcamp.socialnetwork.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Create datasource using Hikari connection pooling, EntityManagerFactory,
 * sets up transaction manager and refers to Spring-data repository.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.bootcamp.socialnetwork.repository")
@EnableTransactionManagement
public class JpaConfiguration {

    @Autowired
    private Environment environment;

    @Value("${datasource.app.maxPoolSize:10}")
    private int maxPoolSize;


    /*
     * Populate SpringBoot DataSourceProperties object directly from application.yml
     * based on prefix. Thanks to .yml, hierarchical data is mapped out of the box with
     * matching-name properties of DataSourceProperties object.
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource.app")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /*
     * Configure HikariCP pooled DataSource.
     */
    @Bean
    public DataSource dataSource() {
        DataSourceProperties dataSourceProperties = dataSourceProperties();
        HikariDataSource dataSource = (HikariDataSource) DataSourceBuilder
                .create(dataSourceProperties.getClassLoader())
                .driverClassName(dataSourceProperties.getDriverClassName())
                .url(dataSourceProperties.getUrl())
                .username(dataSourceProperties.getUsername())
                .password(dataSourceProperties.getPassword())
                .type(HikariDataSource.class)
                .build();
        dataSource.setMaximumPoolSize(maxPoolSize);
        return dataSource;
    }

    /*
     * Entity Manager Factory setup.
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws NamingException {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setPackagesToScan("com.bootcamp.socialnetwork.domain");
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        factoryBean.setJpaProperties(jpaProperties());
        return factoryBean;
    }

    /*
     * Provide specific adapter.
     */
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    /*
     * Provide specific properties.
     */
    private Properties jpaProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect",
                environment.getRequiredProperty("datasource.app.hibernate.dialect"));
        properties.put("hibernate.hbm2ddl.auto",
                environment.getRequiredProperty("datasource.app.hibernate.hbm2ddl.method"));
        properties.put("hibernate.show_sql",
                environment.getRequiredProperty("datasource.app.hibernate.show_sql"));
        properties.put("hibernate.format_sql",
                environment.getRequiredProperty("datasource.app.hibernate.format_sql"));
        if (StringUtils.isNotEmpty(environment.getRequiredProperty("datasource.app.defaultSchema"))) {
            properties.put("hibernate.default_schema",
                    environment.getRequiredProperty("datasource.app.defaultSchema"));
        }
        return properties;
    }

    @Bean
    @Autowired
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
