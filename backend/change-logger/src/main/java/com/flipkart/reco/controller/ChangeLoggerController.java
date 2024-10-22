package com.flipkart.reco.controller;

import com.flipkart.reco.service.ConfigChangeLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/")
public class ChangeLoggerController {
    @Autowired
    private ConfigChangeLoggerService configChangeLoggerService;

    @GetMapping("/restart")
    public void restart() {
        configChangeLoggerService.restartOps();
    }
}
