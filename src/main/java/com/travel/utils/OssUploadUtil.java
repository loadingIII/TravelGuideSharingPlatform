package com.travel.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.travel.config.OssProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OssUploadUtil {

    private final OssProperties ossProperties;
    private OSS ossClient;

    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );
        log.info("OSS client initialized, endpoint: {}", ossProperties.getEndpoint());
    }

    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String objectName = "uploads/" + UUID.randomUUID().toString().replace("-", "") + ext;

        try (InputStream inputStream = file.getInputStream()) {
            ossClient.putObject(ossProperties.getBucketName(), objectName, inputStream);
            String url = "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/" + objectName;
            log.info("文件上传OSS成功: {} -> {}", originalFilename, url);
            return url;
        } catch (IOException e) {
            log.error("文件上传OSS失败", e);
            throw new RuntimeException("文件上传OSS失败", e);
        }
    }

    /**
     * 从完整OSS URL中下载文件，返回OSSObject（包含InputStream和元数据）
     */
    public OSSObject download(String ossUrl) {
        String objectName = extractObjectName(ossUrl);
        log.info("从OSS下载文件: {}", objectName);
        return ossClient.getObject(ossProperties.getBucketName(), objectName);
    }

    /**
     * 从完整URL中提取objectName
     * URL格式: https://{bucket}.{endpoint}/{objectName}
     */
    private String extractObjectName(String ossUrl) {
        String prefix = "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/";
        if (ossUrl.startsWith(prefix)) {
            return ossUrl.substring(prefix.length());
        }
        // 兼容：直接取最后一个 / 之后的部分
        int lastSlash = ossUrl.lastIndexOf('/');
        if (lastSlash >= 0) {
            return ossUrl.substring(lastSlash + 1);
        }
        throw new IllegalArgumentException("无法从URL中解析objectName: " + ossUrl);
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
            log.info("OSS client shutdown");
        }
    }
}
