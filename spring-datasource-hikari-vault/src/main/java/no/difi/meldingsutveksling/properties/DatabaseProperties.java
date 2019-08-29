package no.difi.meldingsutveksling.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "no.nav.db")
public class DatabaseProperties {
    private String name;
    private String url;

    public String getName() { return name; }
    public String getUrl() { return url; }
    public void setName(String name) { this.name = name; }
    public void setUrl(String url) { this.url = url; }
}
