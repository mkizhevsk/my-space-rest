package com.mk.myspacerest;

import com.mk.myspacerest.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class MySpaceRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(MySpaceRestApplication.class, args);
	}

}
