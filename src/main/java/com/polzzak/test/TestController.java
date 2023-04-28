package com.polzzak.test;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

    @GetMapping("/static/files")
    public ResponseEntity<List<String>> getStaticFiles() {
        List<String> fileList = new ArrayList<>();

        // static 디렉토리 경로를 구합니다.
        String path = getClass().getResource("/static").getPath();
        File directory = new File(path);

        // static 디렉토리 내의 모든 파일과 디렉토리 목록을 구합니다.
        for (File file : directory.listFiles()) {
            fileList.add(file.getName());
            if (file.isDirectory()) {
                for (File subFile : file.listFiles()) {
                    fileList.add(file.getName() + "/" + subFile.getName());
                }
            }
        }


        return ResponseEntity.ok(fileList);
    }
}
