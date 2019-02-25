package com.dppware.centralizedConfigServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableConfigServer
public class CentralizedConfigServerApplication {

	public static void main(String[] arguments) {
        SpringApplication.run(CentralizedConfigServerApplication.class, arguments);
    }

}
