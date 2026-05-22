package ru.chuchkalov.taskmanager.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.security.JwtConfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@AllArgsConstructor
public class TokenService {

    private JwtConfig jwtConfig;

    public String generateToken(Authentication authentication) {
        var header = new JWSHeader.Builder(jwtConfig.getJwtAlgorithm())
                .type(JOSEObjectType.JWT)
                .build();
        Instant now = Instant.now();
        var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        var user = (User) authentication.getPrincipal();

        var builder = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("taskManager")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(jwtConfig.getJwtExpiration(), ChronoUnit.MINUTES)));
        builder.claim("roles", roles);

        builder.claim("username", user.getUsername());
        builder.claim("email", user.getEmail());
        builder.claim("id", user.getId());

        var claims = builder.build();

        var key = jwtConfig.getSecretKey();
        var jwt = new SignedJWT(header, claims);

        MACSigner signer = null;
        try {
            signer = new MACSigner(key);
            jwt.sign(signer);
        } catch (JOSEException e) {
            throw new RuntimeException("Error generation JWT", e);
        }

        return jwt.serialize();

    }

}
