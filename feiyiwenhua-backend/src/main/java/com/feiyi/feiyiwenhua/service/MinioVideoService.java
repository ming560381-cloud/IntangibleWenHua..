package com.feiyi.feiyiwenhua.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioVideoService {
    String uploadHeritageVideo(Long heritageId, MultipartFile file);
}
