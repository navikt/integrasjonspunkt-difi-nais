package no.difi.meldingsutveksling;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.LogicalResponse;
import no.difi.meldingsutveksling.properties.VaultProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class VaultHelper {
    private final Vault vault;
    private final VaultProperties vaultProperties;

    public VaultHelper(VaultProperties vaultProperties) throws Exception {
        this.vaultProperties = vaultProperties;
        vault = new Vault(new VaultConfig()
                .address(vaultProperties.getUrl())
                .token(Files.readString(Paths.get(vaultProperties.getTokenPath())))
                .openTimeout(5)
                .readTimeout(30)
                .sslConfig(new SslConfig().build())
                .build());
    }
    DatasourceCredentials fetchCredentials(String dbName, String role) throws VaultException {
        LogicalResponse credentials = vault.logical().read(String.format("%s/creds/%s-%s", vaultProperties.getTokenPath(), dbName, role));

        return new DatasourceCredentials(credentials.getData().get("username"), credentials.getData().get("password"));
    }

    static class DatasourceCredentials {
        final String username;
        final String password;

        DatasourceCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
