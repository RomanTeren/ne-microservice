package com.ne.microservice.config;

import com.ne.microservice.security.AuthoritiesConstants;
import com.ne.microservice.security.DomainJwtAccessTokenConverter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

@Configuration
@EnableResourceServer
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends ResourceServerConfigurerAdapter {

    private final DiscoveryClient discoveryClient;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
            .disable()
            .headers()
            .frameOptions()
            .disable()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/**").authenticated()
            .antMatchers("/management/health").permitAll()
            .antMatchers("/management/info").permitAll()
            .antMatchers("/management/prometheus").permitAll()
            .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN);
    }

    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(
        @Qualifier("loadBalancedRestTemplate") RestTemplate keyUriRestTemplate)
        throws CertificateException, IOException {

        DomainJwtAccessTokenConverter converter = new DomainJwtAccessTokenConverter();
        converter.setVerifierKey(getKeyFromConfigServer(keyUriRestTemplate));
        return converter;
    }

    @Bean
    @Qualifier("loadBalancedRestTemplate")
    public RestTemplate loadBalancedRestTemplate(RestTemplateCustomizer customizer) {
        RestTemplate restTemplate = new RestTemplate();
        customizer.customize(restTemplate);
        return restTemplate;
    }

    @Bean
    @Qualifier("vanillaRestTemplate")
    public RestTemplate vanillaRestTemplate() {
        return new RestTemplate();
    }

    private String getKeyFromConfigServer(RestTemplate keyUriRestTemplate) throws CertificateException, IOException {
        // Load available UAA servers
        discoveryClient.getServices();
        HttpEntity<Void> request = new HttpEntity<Void>(new HttpHeaders());
        String content = keyUriRestTemplate
            .exchange("http://config/api/token_key", HttpMethod.GET, request, String.class).getBody();

        if (StringUtils.isBlank(content)) {
            throw new CertificateException("Received empty certificate from config.");
        }

        try (InputStream fin = new ByteArrayInputStream(content.getBytes())) {

            CertificateFactory f = CertificateFactory.getInstance(Constants.CERTIFICATE);
            X509Certificate certificate = (X509Certificate) f.generateCertificate(fin);
            PublicKey pk = certificate.getPublicKey();
            return String.format(Constants.PUBLIC_KEY, new String(Base64.getEncoder().encode(pk.getEncoded())));
        }
    }
}
