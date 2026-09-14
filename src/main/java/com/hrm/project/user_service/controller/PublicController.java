package com.hrm.project.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/public")
public class PublicController {

    @GetMapping
    public ResponseEntity<String> getApiNumber(){
        return new ResponseEntity<>("Current api is running ok", HttpStatus.OK);
    }
}
