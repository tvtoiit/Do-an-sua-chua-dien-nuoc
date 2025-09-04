package com.example.CuoiKi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "CuoiKi API", version = "1.0", description = "API for authentication"))
public class CuoiKiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CuoiKiApplication.class, args);
    }
}

