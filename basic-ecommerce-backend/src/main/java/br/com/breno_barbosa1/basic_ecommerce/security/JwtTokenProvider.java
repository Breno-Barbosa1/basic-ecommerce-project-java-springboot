package br.com.breno_barbosa1.basic_ecommerce.security;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.security.TokenDTO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {

    @Autowired
    UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Value("${security.jwt.secret-key:admin123}")
    private String jwtSecret;

    @Value("${security.jwt.expire-length:3600000}")
    private long expirationInMilliseconds;

    Algorithm algorithm = null;

    @PostConstruct
    protected void init() {
        byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
        algorithm = Algorithm.HMAC256(decodedKey);
    }

    public TokenDTO generateAccessToken(String email, List<String> roles) {
        Date currentTime = new Date();
        Date expirationTime = new Date(currentTime.getTime() + expirationInMilliseconds);
        String accessToken = obtainAccessToken(email, roles, currentTime, expirationTime);
        String refreshToken = obtainRefreshToken(email, currentTime);
        return new TokenDTO(email, true, currentTime, expirationTime, accessToken, refreshToken);
    }

    public TokenDTO refreshToken(String refreshToken) {
        var token = "";
        if (!refreshToken.isBlank() && refreshToken.contains("Bearer ")) {
            token = refreshToken.substring("Bearer ".length());
        }

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        String email = decodedJWT.getSubject();
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        return generateAccessToken(email, roles);
    }


    public String obtainAccessToken(String email, List<String> roles, Date currentTime, Date expirationTime) {
        String issuer = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return JWT.create()
            .withClaim("roles", roles)
            .withIssuer(issuer)
            .withIssuedAt(currentTime)
            .withExpiresAt(expirationTime)
            .withSubject(email)
            .sign(algorithm);
    }

    public String obtainRefreshToken(String email, Date currentTime) {
        Date refreshTokenExpirationTime = new Date(currentTime.getTime() + (expirationInMilliseconds * 3));

        return JWT.create()
            .withIssuedAt(currentTime)
            .withExpiresAt(refreshTokenExpirationTime)
            .withSubject(email)
            .sign(algorithm);
    }

    public DecodedJWT decodedToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public boolean validateToken(String token) {
        try {
            decodedToken(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String returnValidToken(HttpServletRequest httpServletRequest) {
        String header = httpServletRequest.getHeader("Authorization");

        if (header == null || header.isBlank() || !header.startsWith("Bearer ")) {
            return "";
        }
        return header.substring(7);
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = decodedToken(token);

        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(decodedJWT.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
}