package com.culcon.backend.configs.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
	entityManagerFactoryRef = "recordEntityManager",
	transactionManagerRef = "recordTransactionManager",
	basePackages = {
		"com.culcon.backend.repositories.record"
	}
)
public class RecordDatabase {
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.record")
	public DataSource recordDataSource() {
		return new DriverManagerDataSource();
	}

	@Bean(name = "recordEntityManager")
	public LocalContainerEntityManagerFactoryBean recordEntityManager() {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();

		bean.setDataSource(recordDataSource());
		bean.setPackagesToScan("com.culcon.backend.models.record");

		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setGenerateDdl(true);
		bean.setJpaVendorAdapter(adapter);

		return bean;
	}


	@Bean(name = "recordTransactionManager")
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager manager = new JpaTransactionManager();
		manager.setEntityManagerFactory(recordEntityManager().getObject());
		return manager;
	}
}
