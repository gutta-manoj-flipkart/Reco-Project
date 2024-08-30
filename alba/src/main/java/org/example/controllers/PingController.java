package org.example.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class PingController {
        @PostMapping("/ping")
        public ResponseEntity<?> ping(@RequestBody String json) {
            System.out.println("Received: " + json);
            return ResponseEntity.ok("Successful");
        }
    }
