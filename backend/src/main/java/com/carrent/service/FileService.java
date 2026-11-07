package com.carrent.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务
 */
public interface FileService {

    /**
     * 保存上传的图片文件，返回可访问的 URL 路径（如 /uploads/xxx.jpg）
     */
    String upload(MultipartFile file);
}
