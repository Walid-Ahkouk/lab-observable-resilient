package com.example.pricingservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/prices")
public class PricingController {

    @GetMapping("/{bookId}")
    public Map<String, Object> getPrice(@PathVariable("bookId") Long bookId) {
        // Mock price generation logic
        // For simplicity, returning a fixed or semi-random price
        double price = 10.0 + (bookId % 10); 
        return Map.of(
            "bookId", bookId,
            "price", price
        );
    }
}
