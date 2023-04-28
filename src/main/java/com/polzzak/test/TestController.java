package com.polzzak.test;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
    public ResponseEntity<?> getStaticFiles(@RequestParam String path) {
        String rootPath = path; // 실행 디렉토리를 루트 디렉토리로 설정
        File rootDirectory = new File(rootPath);
        List<String> fileList = new ArrayList<>();

        createFileList(rootDirectory, "", fileList);

        String a = "";

        for (String s : fileList) {
            a = a + s + "</br>";
        }

        System.out.println(a);
        return ResponseEntity.ok(a);
    }

    private void createFileList(File directory, String prefix, List<String> fileList) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String directoryName = file.getName();
//                    fileList.add(prefix + directoryName + "/");
                    createFileList(file, prefix + "  ", fileList);
                } else {
                    String fileName = file.getName();
                    String filePath = file.getAbsolutePath();
                    String relativePath = new File("").getAbsolutePath(); // 실행 디렉토리
                    relativePath = relativePath.substring(0, relativePath.length() - 1); // 끝에 "/" 제거
                    filePath = filePath.replace(relativePath, ""); // 실행 디렉토리 경로 제거
                    fileList.add(prefix  + " (" + filePath + ")");
                }
            }
        }
    }
}