package no.difi.meldingsutveksling;

import com.zaxxer.hikari.HikariDataSource;
import no.difi.meldingsutveksling.properties.DatabaseProperties;
import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;

@Configuration
public class HikariVaultDataSourceOverride {
    static final String ROLE_USER = "user";
    private static final String ROLE_ADMIN = "admin";

    private final DatabaseProperties databaseProperties;
    private final VaultHelper vaultHelper;

    public HikariVaultDataSourceOverride(DatabaseProperties databaseProperties, VaultHelper vaultHelper) {
        this.databaseProperties = databaseProperties;
        this.vaultHelper = vaultHelper;
    }

    @Bean
    public DataSource dataSource() throws Exception {
        return createDataSource(ROLE_USER);
    }

    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);

        return sessionFactoryBean.getObject();
    }

    @LiquibaseDataSource
    @Bean
    public DataSource liquibaseDataSource() throws Exception {
        HikariDataSource dataSource = createDataSource(ROLE_ADMIN);
        dataSource.setConnectionInitSql(String.format("SET ROLE \"%s-%s\"", databaseProperties.getName(), ROLE_ADMIN));
        return createDataSource(ROLE_ADMIN);
    }

    private HikariDataSource createDataSource(String role) throws Exception {
        VaultHelper.DatasourceCredentials credentials = vaultHelper.fetchCredentials(databaseProperties.getName(), role);
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(databaseProperties.getUrl());
        dataSource.getHikariConfigMXBean().setUsername(credentials.username);
        dataSource.getHikariConfigMXBean().setPassword(credentials.password);
        dataSource.getHikariPoolMXBean().softEvictConnections();
        dataSource.validate();
        return dataSource;
    }
}
