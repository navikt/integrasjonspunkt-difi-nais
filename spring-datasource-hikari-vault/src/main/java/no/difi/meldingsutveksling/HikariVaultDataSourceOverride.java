package no.difi.meldingsutveksling;

import com.zaxxer.hikari.HikariDataSource;
import no.difi.meldingsutveksling.properties.DatabaseProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Properties;

@Configuration
public class HikariVaultDataSourceOverride {
    static final String ROLE_USER = "user";
    static final String ROLE_ADMIN = "admin";

    private final DatabaseProperties databaseProperties;
    private final VaultHelper vaultHelper;

    public HikariVaultDataSourceOverride(DatabaseProperties databaseProperties, VaultHelper vaultHelper) {
        this.databaseProperties = databaseProperties;
        this.vaultHelper = vaultHelper;
    }

    @Primary
    @Bean
    public DataSource dataSource() throws Exception { // TODO: Hacky workaround, use admin for write to schemas
        HikariDataSource dataSource = createDataSource(ROLE_ADMIN);
        dataSource.setConnectionInitSql(String.format("SET ROLE \"%s-%s\"", databaseProperties.getName(), ROLE_ADMIN));
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            DataSource dataSource
    ) {
        HashMap<String, String> hibernateProperties = new HashMap<>();

        hibernateProperties.put("hibernate.hbm2ddl.auto", "update");
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
//        hibernateProperties.put("hibernate.show_sql", "true"); Enable logging

        return builder
                .dataSource(dataSource)
                .packages("no.difi.meldingsutveksling")
                .properties(hibernateProperties)
                .build();
    }

    @LiquibaseDataSource
    @Bean
    public DataSource liquibaseDataSource() throws Exception {
        HikariDataSource dataSource = createDataSource(ROLE_ADMIN);
        dataSource.setConnectionInitSql(String.format("SET ROLE \"%s-%s\"", databaseProperties.getName(), ROLE_ADMIN));
        return dataSource;
    }

    private HikariDataSource createDataSource(String role) throws Exception {
        VaultHelper.DatasourceCredentials credentials = vaultHelper.fetchCredentials(databaseProperties.getName(), role);
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(databaseProperties.getUrl());
        dataSource.getHikariConfigMXBean().setUsername(credentials.username);
        dataSource.getHikariConfigMXBean().setPassword(credentials.password);
        dataSource.validate();
        return dataSource;
    }
}
