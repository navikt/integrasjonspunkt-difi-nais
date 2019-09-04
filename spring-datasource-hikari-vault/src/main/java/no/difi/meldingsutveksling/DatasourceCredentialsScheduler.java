package no.difi.meldingsutveksling;

import com.zaxxer.hikari.HikariDataSource;
import no.difi.meldingsutveksling.properties.DatabaseProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DatasourceCredentialsScheduler {
    private final DatabaseProperties databaseProperties;
    private final HikariDataSource dataSource;
    private final VaultHelper vaultHelper;
    public DatasourceCredentialsScheduler(
            DatabaseProperties databaseProperties,
            HikariDataSource dataSource,
            VaultHelper vaultHelper
    ) {
        this.dataSource = dataSource;
        this.vaultHelper = vaultHelper;
        this.databaseProperties = databaseProperties;
    }

    @Scheduled(fixedDelay = 4*1000)
    public void updateDataSourceCredentials() throws Exception {
        VaultHelper.DatasourceCredentials credentials = vaultHelper.fetchCredentials(databaseProperties.getName(), HikariVaultDataSourceOverride.ROLE_ADMIN);
        dataSource.getHikariConfigMXBean().setUsername(credentials.username);
        dataSource.getHikariConfigMXBean().setPassword(credentials.password);
        dataSource.getHikariPoolMXBean().softEvictConnections();
    }
}
