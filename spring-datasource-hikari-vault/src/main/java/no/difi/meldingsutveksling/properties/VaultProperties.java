package no.difi.meldingsutveksling.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "no.nav.vault")
public class VaultProperties {
    private String url = "https://vault.adeo.no";
    private String tokenPath = "/var/run/secrets/nais.io/vault/vault_token";
    private String kvPath;

    public String getTokenPath() { return tokenPath; }
    public String getUrl() { return url; }
    public String getKvPath() { return kvPath; }

    public void setTokenPath(String tokenPath) { this.tokenPath = tokenPath; }
    public void setUrl(String url) { this.url = url; }
    public void setKvPath(String kvPath) { this.kvPath = kvPath; }
}
