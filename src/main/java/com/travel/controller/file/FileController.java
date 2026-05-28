package com.travel.controller.file;

import com.aliyun.oss.model.OSSObject;
import com.travel.pojo.common.ApiResponse;
import com.travel.utils.OssUploadUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final OssUploadUtil ossUploadUtil;

    @PostMapping("/upload")
    public ApiResponse<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.fail("FILE_EMPTY", "文件不能为空");
        }
        try {
            String url = ossUploadUtil.upload(file);
            return ApiResponse.success(url);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return ApiResponse.fail("UPLOAD_FAIL", "文件上传失败");
        }
    }

    @GetMapping("/download")
    public void download(@RequestParam("url") String ossUrl, HttpServletResponse response) {
        try {
            OSSObject ossObject = ossUploadUtil.download(ossUrl);
            String objectName = ossObject.getKey();
            String fileName = objectName.substring(objectName.lastIndexOf('/') + 1);
            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentLengthLong(ossObject.getObjectMetadata().getContentLength());

            try (InputStream in = ossObject.getObjectContent();
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            log.error("文件下载失败: {}", ossUrl, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
