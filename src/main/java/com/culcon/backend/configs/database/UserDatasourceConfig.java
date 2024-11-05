package com.culcon.backend.configs.database;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
// @EnableTransactionManagement
// @EnableJpaRepositories(
// basePackageClasses = { Account.class },
// entityManagerFactoryRef = "userDatabaseEntityManagerFactory",
// transactionManagerRef = "userDatabaseTransactionManager")
public class UserDatasourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.userdb")
    public DataSourceProperties userDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public HikariDataSource userDataSourceBuilder(
            DataSourceProperties uDataSourceProperties) {
        return userDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    // @Bean
    // public JdbcTemplate userDataSourceJdbcTemplate(
    // @Qualifier("userDataSource")
    // DataSource dataSource) {
    // return new JdbcTemplate();
    // }

    // @Bean
    // public LocalContainerEntityManagerFactoryBean userDataEntityManagerFactory(
    // @Qualifier("userDataSource")
    // DataSource dataSource,
    // EntityManagerFactoryBuilder builder) {
    // return builder
    // .dataSource(dataSource)
    // .packages(Account.class)
    // .build();
    // }
}
