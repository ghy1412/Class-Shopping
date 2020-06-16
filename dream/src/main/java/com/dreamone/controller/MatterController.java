package com.dreamone.controller;

import com.dreamone.error.BusinessException;
import com.dreamone.error.EmBusinessError;
import com.dreamone.response.CommonReturnType;
import com.dreamone.service.ImageService;
import com.dreamone.service.model.ImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller("matter")
@RequestMapping("/matter")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class MatterController extends BaseController {

    @Autowired
    private ImageService matterService;

    @RequestMapping(value = "/uploadImage")
    @ResponseBody
    public CommonReturnType uploadImage(@RequestParam(name = "file") MultipartFile file) throws BusinessException, IOException {
        if (file == null) {
            throw new BusinessException(EmBusinessError.UPLOAD_FAIL);
        }
        matterService.upload(file,0);

        return CommonReturnType.create(null);
    }


    @RequestMapping(value = "/showImage",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType showImage() throws BusinessException, IOException {

        List<ImageModel> imageModelList = matterService.selectImage(0, 4);
        return CommonReturnType.create(imageModelList);
    }


}
