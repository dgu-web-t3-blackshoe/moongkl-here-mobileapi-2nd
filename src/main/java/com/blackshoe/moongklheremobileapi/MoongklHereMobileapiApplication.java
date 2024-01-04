package com.blackshoe.moongklheremobileapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MoongklHereMobileapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoongklHereMobileapiApplication.class, args);
    }

}
