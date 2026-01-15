package com.joelcode.pokerchipsapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PokerChipsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PokerChipsApplication.class, args);
    }

    @GetMapping
    public String Test(){
        return "Test";
    }

}