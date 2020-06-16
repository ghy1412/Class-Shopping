package com.dreamone.service.impl;

import com.dreamone.dao.ImageDOMapper;
import com.dreamone.dataobject.ImageDO;
import com.dreamone.error.BusinessException;
import com.dreamone.service.ImageService;
import com.dreamone.service.model.ImageModel;
import com.dreamone.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageDOMapper imageDOMapper;

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public Integer upload(MultipartFile multipartFile, Integer userId) throws BusinessException, IOException {
        ImageModel imageModel = new ImageModel();

/*        UserModel user_login = (UserModel)this.httpServletRequest.getSession().getAttribute("USER_LOGIN");
        if (user_login == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }*/

        // 获取文件名，带后缀
        String originalFilename = multipartFile.getOriginalFilename();

        // 只有当满足图片格式时才进来，重新赋图片名，防止出现名称重复的情况
        String newFileName = originalFilename;
        // 该方法返回的为当前项目的工作目录，即在哪个地方启动的java线程
        String dirPath = System.getProperty("user.dir");
        //String path = File.separator + "uploadImg" + File.separator + newFileName;
        String path = File.separator + "src"+File.separator+"main"+File.separator
                +"resources"+File.separator+"static";
        String dbPath =  File.separator +"img"+File.separator+newFileName;

        imageModel.setPath("."+dbPath);
        File destFile = new File(dirPath + path+dbPath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }


        multipartFile.transferTo(destFile);

        imageModel.setName(originalFilename);

        LocalDateTime now = LocalDateTime.now();
        String format = now.format(DateTimeFormatter.ISO_DATE);
        String nowDate = format.replace("-", "");
        imageModel.setLoadTime(nowDate);

        ImageDO imageDO = convertDOFormModel(imageModel);
        imageDO.setUserId(userId);
        imageDOMapper.insertSelective(imageDO);

        return imageDO.getId();
    }

    private ImageDO convertDOFormModel(ImageModel imageModel) {
        if (imageModel == null) {
            return null;
        }
        ImageDO imageDO = new ImageDO();
        BeanUtils.copyProperties(imageModel,imageDO);
        return imageDO;
    }

    private ImageModel convertModelFormDo(ImageDO imageDO) {
        if (imageDO == null) {
            return null;
        }
        ImageModel imageModel = new ImageModel();
        BeanUtils.copyProperties(imageDO,imageModel);
        return imageModel;
    }

    @Override
    public List<ImageModel> selectImage(Integer first, Integer last) {
        List<ImageDO> imageDOS = imageDOMapper.selectImage(first, last);
        ArrayList<ImageModel> imageModels = new ArrayList<>();
        imageDOS.forEach(imageDO -> {
            ImageModel imageModel = convertModelFormDo(imageDO);
            imageModels.add(imageModel);
            System.out.println(imageModel.getPath());
        });
        return imageModels;
    }
}
