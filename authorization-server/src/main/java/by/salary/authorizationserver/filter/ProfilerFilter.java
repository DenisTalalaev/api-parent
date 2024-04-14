package by.salary.authorizationserver.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;


@Profile("dev")
@Component
@Order(-1000)
@Slf4j
public class ProfilerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Date start = new Date();

        filterChain.doFilter(request, response);

        Date end = new Date();
        log.info("Request {} took {} ms", request.getRequestURI(), end.getTime() - start.getTime());
    }

}