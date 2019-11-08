package no.difi.meldingsutveksling;

import com.zaxxer.hikari.HikariDataSource;
import no.difi.meldingsutveksling.properties.DatabaseProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class DatasourceCredentialsScheduler {
	private final DatabaseProperties databaseProperties;
	private final HikariDataSource dataSource;
	private final HikariDataSource liquibaseDataSource;
	private final HikariDataSource jmsDataSource;
	private final VaultHelper vaultHelper;

	public DatasourceCredentialsScheduler(
			DatabaseProperties databaseProperties,
			@Qualifier("dataSource") HikariDataSource dataSource,
			@Qualifier("liquibaseDataSource") HikariDataSource liquibaseDataSource,
			@Qualifier("jmsDataSource") HikariDataSource jmsDataSource,
			VaultHelper vaultHelper
	) {
		this.dataSource = dataSource;
		this.liquibaseDataSource = liquibaseDataSource;
		this.jmsDataSource = jmsDataSource;
		this.vaultHelper = vaultHelper;
		this.databaseProperties = databaseProperties;
	}

	@Scheduled(fixedDelay = 60 * 1000)
	public void updateDataSourceCredentials() throws Exception {
		VaultHelper.DatasourceCredentials credentials = vaultHelper.fetchCredentials(databaseProperties.getName(), HikariVaultDataSourceOverride.ROLE_ADMIN);
		dataSource.getHikariConfigMXBean().setUsername(credentials.username);
		dataSource.getHikariConfigMXBean().setPassword(credentials.password);
		dataSource.getHikariPoolMXBean().softEvictConnections();
		liquibaseDataSource.getHikariConfigMXBean().setUsername(credentials.username);
		liquibaseDataSource.getHikariConfigMXBean().setPassword(credentials.password);
		liquibaseDataSource.getHikariPoolMXBean().softEvictConnections();
		jmsDataSource.getHikariConfigMXBean().setUsername(credentials.username);
		jmsDataSource.getHikariConfigMXBean().setPassword(credentials.password);
		jmsDataSource.getHikariPoolMXBean().softEvictConnections();
	}
}
