package com.carrent.controller;

import com.carrent.common.Result;
import com.carrent.service.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口（管理端，受 AdminInterceptor 保护）
 */
@RestController
@RequestMapping("/api/admin")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        return Result.success("上传成功", fileService.upload(file));
    }
}
