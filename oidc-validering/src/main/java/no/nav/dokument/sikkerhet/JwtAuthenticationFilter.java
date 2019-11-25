package no.nav.dokument.sikkerhet;

import static no.nav.security.token.support.core.utils.JwtTokenUtil.contextHasValidToken;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException;

public class JwtAuthenticationFilter implements Filter {

	private final TokenValidationContextHolder tokenValidationContextHolder;

	public JwtAuthenticationFilter(
			TokenValidationContextHolder tokenValidationContextHolder) {
		this.tokenValidationContextHolder = tokenValidationContextHolder;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (!contextHasValidToken(tokenValidationContextHolder)) {
			throw new JwtTokenUnauthorizedException(
					"no valid token found in validation context");
		}
		chain.doFilter(request, response);
	}

}
