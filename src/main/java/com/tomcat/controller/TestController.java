package com.tomcat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController 
{
	private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/")
    public String home() {
    	log.info("cicd deployed on tomcat server");
        return "CICD Tomcat deployment working !!!";
    }
    
    @GetMapping("/welcome")
    public String welcome() {
    	log.info("Welcome Endpoint -> cicd deployed on tomcat server");
        return "Welcome Endpoint --> CICD Tomcat deployment working !!!";
    }
    
    
}

