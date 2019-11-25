package no.difi.meldingsutveksling;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.dokument.sikkerhet.JwtTokenValidationConfig;

@Configuration
@Import(JwtTokenValidationConfig.class)
public class JwtAuthenticationConfig {

}
