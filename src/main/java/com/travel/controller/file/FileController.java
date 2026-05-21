package com.travel.controller.file;

import com.travel.pojo.common.ApiResponse;
import com.travel.utils.OssUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
}
