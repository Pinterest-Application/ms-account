package com.example.msaccount.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/test")
    public String getTestResp() {
        return "Hellotoken";
    }

    @GetMapping("/testNOAUTH")
    public String getTestRespNOAUTH() {
        return "tokensiz sorgu";
    }

}
