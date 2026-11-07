package com.carrent.service.impl;

import com.carrent.common.BusinessException;
import com.carrent.service.FileService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

/**
 * 文件上传服务实现：将图片保存到本地 uploads 目录，返回访问路径
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_SIZE = 10 * 1024 * 1024L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Value("${file.upload-dir}")
    private String uploadDir;

    /** 启动时创建上传目录，保证静态资源映射目录存在 */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(getUploadPath());
        } catch (IOException e) {
            throw new IllegalStateException("无法创建上传目录: " + uploadDir, e);
        }
    }

    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("图片大小不能超过 10MB");
        }
        String ext = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException("仅支持 jpg/png/gif/webp 格式图片");
        }

        // 生成唯一文件名，避免不同车辆图片互相覆盖
        String filename = LocalDateTime.now().format(FMT)
                + "_" + UUID.randomUUID().toString().substring(0, 8)
                + "." + ext;
        try {
            Path target = getUploadPath().resolve(filename);
            file.transferTo(target.toFile());
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new BusinessException("图片上传失败");
        }
    }

    private Path getUploadPath() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
