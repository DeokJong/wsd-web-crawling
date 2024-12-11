package com.wsd.web.wsd_web_crawling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WsdWebCrawlingApplication {

	public static void main(String[] args) {
		SpringApplication.run(WsdWebCrawlingApplication.class, args);
	}

}
