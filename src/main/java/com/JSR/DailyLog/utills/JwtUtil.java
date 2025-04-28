package com.JSR.DailyLog.utills;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtUtil {

    private static final String SECRET_KEY = "uR9N7sDkL3mYzTqP1bXvCeG6wJfR0aShU2i8ZnVoKpMdEyQhRgT";


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ","JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 5 minutes expiration time
                .signWith(getSigningKey())
                .compact();
    }


    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }




//    private static final String SECRET_KEY = "uR9N7sDkL3mYzTqP1bXvCeG6wJfR0aShU2i8ZnVoKpMdEyQhRgT";
//    private static  final SecretKey getSigningKey = Keys.hmacShaKeyFor ( SECRET_KEY.getBytes( StandardCharsets.UTF_8 ) );
//    private static final long EXPIRATION_TIME = 1000*60*60;
//
//    🔍 2. Second Setup:
//            ✅ Pros:
//    Concise and Minimal: Very compact and easy to understand for quick implementations.
//
//    StandardCharsets.UTF_8: Explicit character encoding improves clarity.
//
//❌ Cons:
//    getSigningKey Defined as Variable: It’s a static field but used like a method (getSigningKey()), which would cause a compilation error unless renamed or used correctly.
//
//            java
//            Copy
//    Edit
//// It should be used as:
//    signWith(getSigningKey, SignatureAlgorithm.HS256)
//    No Support for Custom Claims: You can't pass additional data (e.g., roles, permissions) into the token.
//
//    Missing Validation/Parsing: Only generation is shown, so you'll have to implement your own parsing/validation logic later.


//    public String generateToken(String username){
//        return  Jwts.builder ()
//                .setSubject ( username )
//                .setIssuedAt ( new Date (  ) )
//                .setExpiration ( new Date ( System.currentTimeMillis () + EXPIRATION_TIME ) )
//                .signWith ( getSigningKey () , SignatureAlgorithm.HS256 )
//                .compact ();
//
//    }

}
