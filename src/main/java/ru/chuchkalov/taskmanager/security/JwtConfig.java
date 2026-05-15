package ru.chuchkalov.taskmanager.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {

    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;
    @Getter
    @Value("${spring.security.jwt.expiration-time}")
    private long jwtExpiration;
    @Value("${spring.security.jwt.algorithm}")
    private String algorithm;

    public SecretKey getSecretKey() {
        var key = new OctetSequenceKey.Builder(secretKey.getBytes())
                .algorithm(new JWSAlgorithm(algorithm))
                .build();
        return key.toSecretKey();
    }

    public JWSAlgorithm getJwtAlgorithm() {
        return new JWSAlgorithm(algorithm);
    }

}
