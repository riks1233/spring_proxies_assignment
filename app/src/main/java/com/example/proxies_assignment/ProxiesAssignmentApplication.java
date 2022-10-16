package com.example.proxies_assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// Remove automatically configured basic auth. This might be bad for overall
// security. Should have researched more thoroughly.
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class ProxiesAssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProxiesAssignmentApplication.class, args);
    }
}
