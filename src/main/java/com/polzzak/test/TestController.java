package com.polzzak.test;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class TestController {

    @GetMapping("/a")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("new Hello");
    }

    @GetMapping("/{path}/rest")
    public ResponseEntity<Map<String, Object>> rest(@PathVariable String path, @RequestParam int param) {
        Map<String, Object> data = Map.of("path", path, "param", param);
        return ResponseEntity.ok(data);
    }
}
