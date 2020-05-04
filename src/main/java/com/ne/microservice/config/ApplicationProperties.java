package com.ne.microservice.config;

import com.icthh.xm.commons.lep.TenantScriptStorage;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * Properties specific to Nemicroservice.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@Data
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private boolean kafkaEnabled;
    private String kafkaSystemTopic;
    private String kafkaSystemQueue;
    private boolean timelinesEnabled;

    private final Lep lep = new Lep();
    private final EmulatorProperties emulator = new EmulatorProperties();

    private List<String> tenantIgnoredPathList = Collections.emptyList();

    @Getter
    @Setter
    public static class Lep {
        private TenantScriptStorage tenantScriptStorage;
        private String lepResourcePathPattern;
    }

    @Data
    public static class EmulatorProperties {
        private String login;
        private String password;
    }
}
