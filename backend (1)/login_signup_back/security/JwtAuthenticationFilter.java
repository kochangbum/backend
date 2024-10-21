package com.example.login_signup_back.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class JwtAuthenticationFilter extends GenericFilterBean {

	private final JwtTokenProvider jwtTokenProvider;
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);  // 로거 추가

	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void doFilter(ServletRequest request,
											 ServletResponse response,
											 FilterChain chain)
											 throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String token = jwtTokenProvider.resolveToken(httpRequest);



		if (token != null && jwtTokenProvider.validateToken(token)) {

			SecurityContextHolder.getContext()
			.setAuthentication(jwtTokenProvider.getAuthentication(token));
		} else if (token != null) {
			logger.warn("토큰이 유효하지 않음: {}", token);
		} else {
			logger.info("토큰이 존재하지 않음.");
		}

		chain.doFilter(request, response);
	}
}
