package no.nav.dokument.sikkerhet;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration;
import no.nav.security.token.support.core.configuration.ProxyAwareResourceRetriever;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.filter.JwtTokenExpiryFilter;
import no.nav.security.token.support.filter.JwtTokenValidationFilter;
import no.nav.security.token.support.spring.EnableJwtTokenValidationConfiguration;
import no.nav.security.token.support.spring.MultiIssuerProperties;
import no.nav.security.token.support.spring.SpringTokenValidationContextHolder;

/**
 * Spring configuration for using token-support to authenticate URL access.
 * 
 * That is, without annotations and Spring Boot auto-configuration. We cannot
 * simply import or enable the original auto configuration as it is registering
 * a Web MVC interceptor that insists of the presence of annotations. However,
 * we want to reuse as much of the original configuration as possible, by
 * delegation, but that leads to duplication.
 */
@Configuration
@EnableConfigurationProperties(MultiIssuerProperties.class)
public class JwtTokenValidationConfig implements WebMvcConfigurer {

	// Kan ikke injiseres p.g.a. oppretting av uønskede (som krever
	// annotasjoner) og unødvendige bønner.
	private final EnableJwtTokenValidationConfiguration tokenSupportConfig;

	private final TokenValidationContextHolder contextHolder;

	public JwtTokenValidationConfig(Environment env) {
		tokenSupportConfig = new EnableJwtTokenValidationConfiguration();
		tokenSupportConfig.setEnvironment(env);
		contextHolder = new SpringTokenValidationContextHolder();
	}

	@Bean
	public MultiIssuerConfiguration multiIssuerConfiguration(
			MultiIssuerProperties issuerProperties,
			ProxyAwareResourceRetriever resourceRetriever) {
		return new MultiIssuerConfiguration(issuerProperties.getIssuer(),
				resourceRetriever);
	}

	@Bean
	public ProxyAwareResourceRetriever oidcResourceRetriever() {
		return tokenSupportConfig.oidcResourceRetriever();
	}

	@Bean
	public JwtTokenValidationFilter tokenValidationFilter(
			MultiIssuerConfiguration config) {
		return tokenSupportConfig.tokenValidationFilter(config, contextHolder);
	}

	@Bean
	@Qualifier("oidcTokenValidationFilterRegistrationBean")
	public FilterRegistrationBean<JwtTokenValidationFilter> oidcTokenValidationFilterRegistrationBean(
			JwtTokenValidationFilter validationFilter) {
		return tokenSupportConfig
				.oidcTokenValidationFilterRegistrationBean(validationFilter);
	}

	@Bean
	public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(
			MultiIssuerConfiguration oidcConfig) {
		FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
				contextHolder);
		registrationBean.setFilter(filter);
		registrationBean.addUrlPatterns("/api/*");
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
		return registrationBean;
	}

	@Bean
	@Qualifier("oidcTokenExpiryFilterRegistrationBean")
	@ConditionalOnProperty(name = "no.nav.security.jwt.expirythreshold", matchIfMissing = false)
	public FilterRegistrationBean<JwtTokenExpiryFilter> oidcTokenExpiryFilterRegistrationBean(
			TokenValidationContextHolder tokenValidationContextHolder,
			@Value("${no.nav.security.jwt.expirythreshold}") long expiryThreshold) {
		return tokenSupportConfig.oidcTokenExpiryFilterRegistrationBean(
				tokenValidationContextHolder, expiryThreshold);
	}

	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}
}
