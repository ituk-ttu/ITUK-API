package ee.ituk.api.config.security;

import ee.ituk.api.login.SessionService;
import ee.ituk.api.login.domain.Session;
import ee.ituk.api.settings.GlobalSettingsService;
import ee.ituk.api.user.UserService;
import ee.ituk.api.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private static final int SEC_IN_MIN = 60;
    private static final int MILLIS_IN_SEC = 1000;
    private static final String AUTHORITIES_KEY = "authorities";
    private static final String SESSION = "sessionCode";
    private static final String ISSUER = "http://www.ituk.ee";

    @Value("${security.signing.key}")
    private String signingKey;

    private final SessionService sessionService;
    private final UserService userService;
    private final GlobalSettingsService globalSettingsService;

    public String generateToken(User user) {
        return doGenerateToken(user);
    }

    boolean validateToken(String token) {
        return !isTokenExpired(token) && validateSession(token);
    }

    String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private String getSessionCode(String token) {
        Claims allClaimsFromToken = getAllClaimsFromToken(token);
        return (String) allClaimsFromToken.get(SESSION);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private String doGenerateToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        List<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Session session = sessionService.createSession(user);
        claims.put(SESSION, session.getCode());
        claims.put(AUTHORITIES_KEY, authorities);
        Integer sessionTimeoutMin = getSessionTimeout();

        return buildJwtToken(claims, sessionTimeoutMin);
    }

    private String buildJwtToken(Claims claims, int tokenTimeoutMin) {
        long currentTimeMillis = System.currentTimeMillis();
        Date expireDate = new Date(currentTimeMillis + tokenTimeoutMin * SEC_IN_MIN * MILLIS_IN_SEC);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(ISSUER)
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .compact();
    }

    private Integer getSessionTimeout() {
        return globalSettingsService.getSessionTimeInMinutes();
    }

    private boolean validateSession(String token) {
        User user = userService.loadInternalUserByUsername(getUsernameFromToken(token));
        return sessionService.checkSession(user, getSessionCode(token));
    }

}
