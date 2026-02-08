package br.com.breno_barbosa1.basic_ecommerce.security;

import br.com.breno_barbosa1.basic_ecommerce.exceptions.InvalidJWTAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JwtTokenFilter extends GenericFilterBean {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String path = req.getServletPath();

        if (path.equals("/api/users/register") ||
                path.startsWith("/auth") ||
                path.startsWith("/swagger-ui") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/v3/api-docs")) {

            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = jwtTokenProvider.returnValidToken(req);

            if (token != null && !token.isBlank()) {
                if (!jwtTokenProvider.validateToken(token)) {
                    throw new InvalidJWTAuthenticationException("Invalid or expired token");
                }

                var auth = jwtTokenProvider.getAuthentication(token);
                if (auth != null) {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
            filterChain.doFilter(request, response);

        } catch (InvalidJWTAuthenticationException e) {
            showErrorResponse((HttpServletResponse) response, e.getMessage());
        }
    }

    private void showErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String json = String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message);

        response.getWriter().write(json);
    }
}