package com.why.oss.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {

    String uploadTeacherAvatar(MultipartFile file);

    String uploadCourseCover(MultipartFile file);

    String uploadBanner(MultipartFile file);

    String uploadUserAvatar(MultipartFile file);

}
