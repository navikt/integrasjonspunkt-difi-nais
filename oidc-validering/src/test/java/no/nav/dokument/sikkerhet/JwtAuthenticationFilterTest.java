package no.nav.dokument.sikkerhet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException;

final class JwtAuthenticationFilterTest {

	private static final String ENCODED_JWT = ""
			+ "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
			+ "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ."
			+ "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

	@Test
	@DisplayName("No valid tokens generates exception")
	final void doFilterWithoutValidTokensThrowsException() throws Throwable {
		Throwable thrown = catchThrowable(
				() -> testDoFilter(mock(TokenValidationContextHolder.class)));
		assertThat(thrown).isInstanceOf(JwtTokenUnauthorizedException.class)
				.hasMessageContaining("no valid token found");
	}

	@Test
	@DisplayName("A single valid token doesn't throw")
	final void doFilterValidTokens() throws Throwable {
		testDoFilter(mockContextHolderWithOneToken());
	}

	private static TokenValidationContextHolder mockContextHolderWithOneToken() {
		TokenValidationContextHolder ctxHolder = mock(
				TokenValidationContextHolder.class);
		Map<String, JwtToken> issuer2TokenMap = Collections
				.singletonMap("isser", new JwtToken(ENCODED_JWT));
		TokenValidationContext tokenValidationContext = new TokenValidationContext(
				issuer2TokenMap);
		when(ctxHolder.getTokenValidationContext())
				.thenReturn(tokenValidationContext);
		return ctxHolder;
	}

	private static void testDoFilter(
			TokenValidationContextHolder tokenValidationContextHolder)
			throws Throwable {
		JwtAuthenticationFilter instans = new JwtAuthenticationFilter(
				tokenValidationContextHolder);
		ServletRequest request = mock(ServletRequest.class);
		ServletResponse response = mock(ServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		instans.doFilter(request, response, chain);
	}

}
