package com.culcon.backend.configs.database;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
	entityManagerFactoryRef = "userEntityManager",
	transactionManagerRef = "userTransactionManager",
	basePackages = {
		"com.culcon.backend.repositories.user"
	}
)
@EntityScan(basePackages = {
	"com.culcon.backend.models.user"
})
public class UserDatabase {
	@Primary
	@Bean(name = "userDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.user")
	public DataSource userDataSource() {
		return new DriverManagerDataSource();
	}

	@Primary
	@Bean(name = "userEntityManager")
	public LocalContainerEntityManagerFactoryBean userEntityManager() throws SQLException {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();


		bean.setDataSource(userDataSource());
		bean.setPackagesToScan("com.culcon.backend.models.user");

		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setGenerateDdl(true);
		bean.setJpaVendorAdapter(adapter);

		return bean;
	}

	@Bean(name = "userTransactionManager")
	@Primary
	public PlatformTransactionManager transactionManager(
		@Qualifier("userEntityManager") EntityManagerFactory userEntityManager
	) {
		//		manager.setEntityManagerFactory(userEntityManager().getObject());
		return new JpaTransactionManager(userEntityManager);
	}
}
