package com.hadoken.jpa.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author yanggj
 * @date 2022/03/09 9:47
 * @version 1.0.0
 */
@Component("auditorAware")
public class AuditorConfig implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.empty();
    }
}
