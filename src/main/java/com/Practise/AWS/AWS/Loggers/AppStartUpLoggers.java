package com.Practise.AWS.AWS.Loggers;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class AppStartUpLoggers {
	@PostConstruct
    public void init() {
        long timestamp = System.currentTimeMillis();
        System.out.println("🕒 Application started at: " + timestamp);
    }
}
