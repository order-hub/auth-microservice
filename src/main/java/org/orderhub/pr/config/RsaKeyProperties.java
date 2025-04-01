package org.orderhub.pr.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class RsaKeyProperties {
    private String privateKeyFile;
    private String publicKeyFile;
}