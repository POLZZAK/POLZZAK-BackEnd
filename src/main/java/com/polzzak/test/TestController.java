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
    public ResponseEntity<?> getStaticFiles() {    String rootPath = "src/main"; // 루트 경로
        File rootDirectory = new File(rootPath); // 루트 디렉토리 객체 생성
        List<String> fileList = new ArrayList<>();

        // 루트 디렉토리부터 하위 디렉토리 및 파일을 재귀적으로 순회하며 파일 목록 생성
        createFileList(rootDirectory, "", fileList);
        String a= "";

        for (String s : fileList) {
            a = a + s + "</br>";
        }

        System.out.println(a);
        return ResponseEntity.ok(a); // 뷰 이름 반환
    }

    private void createFileList(File directory, String prefix, List<String> fileList) {
        File[] files = directory.listFiles(); // 디렉토리 내 파일 목록 조회
        System.out.println(files.length);
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) { // 디렉토리일 경우
                    String directoryName = file.getName();
                    createFileList(file, prefix + "  ", fileList); // 하위 디렉토리 순회
                } else { // 파일일 경우
                    String fileName = file.getName();
                    String filePath = file.getAbsolutePath(); // 파일의 절대 경로 조회
                    System.out.println(filePath);
                    fileList.add(prefix  + " (" + filePath + ")");
                }
            }
        }
    }
}