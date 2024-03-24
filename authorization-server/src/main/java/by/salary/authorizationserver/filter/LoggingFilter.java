package by.salary.authorizationserver.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		log.info("**************************************************************************");
		log.info("Path of the Request Received -----> " + request.getRequestURI());
		log.info("Ip Address Of Requestor -----> " + request.getLocalAddr());
		log.info("**************************************************************************");

		filterChain.doFilter(request, response);

		log.info("**************************************************************************");
		log.info("Path of the Response Status -----> " + response.getStatus());
		log.info("Path of the Response Headers -----> " + response.getHeaderNames());
		log.info("Path of the Response Authorization header -----> " + response.getHeaders("Authorization"));
		log.info("**************************************************************************");
	}
}