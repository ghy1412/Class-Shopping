package com.dreamone.service;

import com.dreamone.error.BusinessException;
import com.dreamone.service.model.ImageModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    //存储图片
    Integer upload(MultipartFile multipartFile, Integer userId) throws BusinessException, IOException;
    //展示图片
    List<ImageModel> selectImage(Integer first, Integer last);
    //根据userid matterid
}
