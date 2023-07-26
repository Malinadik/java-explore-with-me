package ru.practicum.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StatServiceApplication {
    public static void main(String[] args) {
        System.setProperty("server.port", "9090");
        SpringApplication.run(StatServiceApplication.class, args);
    }
}
