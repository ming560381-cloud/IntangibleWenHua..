package com.feiyi.feiyiwenhua.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioImageService {
    String uploadCommunityImage(Long userId, MultipartFile file);
}
