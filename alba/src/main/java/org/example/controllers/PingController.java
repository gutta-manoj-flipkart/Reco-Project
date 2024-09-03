package org.example.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/")
public class PingController {
        @GetMapping
        public ResponseEntity<String> ping() {
            return ResponseEntity.ok("Pong");
        }
        @PostMapping
        public ResponseEntity<?> ping(@RequestBody String json) {
            System.out.println("Received: " + json);
            return ResponseEntity.ok("Successful");
        }
    }
