package com.polzzak.test;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/a")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("HIHI Hello HIHI new Deploy from cicd");
    }
}
