package com.bootcamp.socialnetwork;

import com.bootcamp.socialnetwork.config.JpaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;


@Import(JpaConfiguration.class)
@SpringBootApplication(scanBasePackages = {"com.bootcamp.socialnetwork"})
@EnableScheduling
public class SocialNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialNetworkApplication.class, args);
    }
}
