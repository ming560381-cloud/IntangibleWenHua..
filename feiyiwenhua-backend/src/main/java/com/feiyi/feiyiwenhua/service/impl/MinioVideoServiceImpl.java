package com.feiyi.feiyiwenhua.service.impl;

import com.feiyi.feiyiwenhua.config.MinioProperties;
import com.feiyi.feiyiwenhua.service.MinioVideoService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class MinioVideoServiceImpl implements MinioVideoService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("mp4", "webm", "ogg", "mov", "m4v");

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperties minioProperties;

    @Override
    public String uploadHeritageVideo(Long heritageId, MultipartFile file) {
        validateVideo(file);
        ensureBucketExists();
        String objectName = buildObjectName(heritageId, file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(resolveContentType(file))
                            .build()
            );
            return buildPublicUrl(objectName);
        } catch (Exception exception) {
            throw new RuntimeException("视频上传失败，请检查 MinIO 配置或服务状态。", exception);
        }
    }

    private void validateVideo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请先选择要上传的视频文件。");
        }

        String contentType = file.getContentType();
        String extension = extractExtension(file.getOriginalFilename());
        boolean contentTypeAllowed = contentType != null && contentType.toLowerCase(Locale.ROOT).startsWith("video/");
        boolean extensionAllowed = ALLOWED_EXTENSIONS.contains(extension);

        if (!contentTypeAllowed && !extensionAllowed) {
            throw new RuntimeException("仅支持上传视频文件，建议使用 mp4、webm、ogg、mov 或 m4v 格式。");
        }
    }

    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(minioProperties.getBucket()).build()
            );
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(minioProperties.getBucket()).build()
                );
            }
        } catch (Exception exception) {
            throw new RuntimeException("无法创建或访问 MinIO bucket，请检查配置。", exception);
        }
    }

    private String buildObjectName(Long heritageId, String originalFilename) {
        String extension = extractExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return String.format(
                "heritage/%d/%s-%s.%s",
                heritageId,
                timestamp,
                UUID.randomUUID().toString().replace("-", ""),
                extension
        );
    }

    private String extractExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "mp4";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private String resolveContentType(MultipartFile file) {
        if (StringUtils.hasText(file.getContentType())) {
            return file.getContentType();
        }
        return "video/mp4";
    }

    private String buildPublicUrl(String objectName) {
        String publicBaseUrl = minioProperties.getPublicBaseUrl();
        if (StringUtils.hasText(publicBaseUrl)) {
            return trimTrailingSlash(publicBaseUrl) + "/" + objectName;
        }
        return trimTrailingSlash(minioProperties.getEndpoint()) + "/" + minioProperties.getBucket() + "/" + objectName;
    }

    private String trimTrailingSlash(String value) {
        return value != null && value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
