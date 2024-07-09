package com.gri.agriconnect.controller;



import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.sql.SQLOutput;
import java.util.HashMap;

@RestController
@RequestMapping("/labs")
public class Labs {

    @PostMapping(value="/read", consumes = "application/json")
    public void foo(@RequestBody HashMap ent, @RequestHeader HashMap header){
        System.out.println(ent);
}
}

