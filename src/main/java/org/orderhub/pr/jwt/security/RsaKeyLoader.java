package org.orderhub.pr.jwt.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaKeyLoader {

    @Value("${jwt.private-key-file}")
    private String privateKeyFile;

    @Value("${jwt.public-key-file}")
    private String publicKeyFile;

    @Bean
    public PrivateKey loadPrivateKey() throws Exception {
        // privateKeyFile 경로를 classpath에서 로드
        ClassPathResource resource = new ClassPathResource("properties/" + privateKeyFile);
        byte[] privateKeyBytes = Files.readAllBytes(resource.getFile().toPath());

        String privateKeyPEM = new String(privateKeyBytes);
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedPrivateKey = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedPrivateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }

    @Bean
    public PublicKey loadPublicKey() throws Exception {
        // publicKeyFile 경로를 classpath에서 로드
        ClassPathResource resource = new ClassPathResource("properties/" + publicKeyFile);
        byte[] publicKeyBytes = Files.readAllBytes(resource.getFile().toPath());

        String publicKeyPEM = new String(publicKeyBytes);
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedPublicKey = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(keySpec);
    }
}