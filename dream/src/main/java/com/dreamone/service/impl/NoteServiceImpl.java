package com.dreamone.service.impl;

import com.dreamone.dao.ImageDOMapper;
import com.dreamone.dao.MiddlewareDOMapper;
import com.dreamone.dao.NoteDOMapper;
import com.dreamone.dataobject.MiddlewareDO;
import com.dreamone.dataobject.NoteDO;
import com.dreamone.error.BusinessException;
import com.dreamone.error.EmBusinessError;
import com.dreamone.service.ImageService;
import com.dreamone.service.NoteService;
import com.dreamone.service.model.ImageModel;
import com.dreamone.service.model.MiddlewareModel;
import com.dreamone.service.model.NoteModel;
import com.dreamone.service.model.UserModel;
import com.dreamone.validator.ValidationResult;
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
public class NoteServiceImpl implements NoteService {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private NoteDOMapper noteDOMapper;

    @Autowired
    private MiddleServiceImpl middleService;
    @Override
    public NoteModel createNote(NoteModel noteModel) throws BusinessException, IOException {
        //校验登陆
        Boolean is_login = (Boolean)httpServletRequest.getSession().getAttribute("IS_LOGIN");

        if (is_login == null || !is_login) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"未登录");
        }

        UserModel userModel = (UserModel)httpServletRequest.getSession().getAttribute("USER_LOGIN");
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
        //校验model的
        ValidationResult validate = validator.validate(noteModel);
        if (validate.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,validate.getErrorMsg());
        }
        //先图片    调用存储note,后调用掺入图片服务 //那服务还是不乱掉了
        String imgUrl = "";
        if (noteModel.getMultipartFile() != null) {
            MultipartFile multipartFile = noteModel.getMultipartFile();

            String originalFilename = multipartFile.getOriginalFilename();

            // 只有当满足图片格式时才进来，重新赋图片名，防止出现名称重复的情况
            String newFileName = originalFilename;
            //先忽略
            // 该方法返回的为当前项目的工作目录，即在哪个地方启动的java线程
            String dirPath = System.getProperty("user.dir");
            //String path = File.separator + "uploadImg" + File.separator + newFileName;
            String path = File.separator + "src"+File.separator+"main"+File.separator
                    +"resources"+File.separator+"static";
            String dbPath =  File.separator +"img"+File.separator+newFileName;

            noteModel.setImgUrl("."+dbPath);
            File destFile = new File(dirPath + path+dbPath);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }

            multipartFile.transferTo(destFile);
        }


        LocalDateTime now = LocalDateTime.now();
        String format = now.format(DateTimeFormatter.ISO_DATE);
        String nowDate = format.replace("-", "");

        noteModel.setCreateTime(nowDate);


        noteModel.setUserId(userModel.getId());
        NoteDO noteDO = convertDoFromModel(noteModel);
        noteDOMapper.insertSelective(noteDO);
        //得到插入的Id
        //现在判断nfaId是否存在, 调用middleware
        if (noteDO.getId() != null && noteModel.getNfaId() != null) {
            MiddlewareModel middlewareModel = new MiddlewareModel();
            middlewareModel.setNoteId(noteModel.getNfaId());;
            middlewareModel.setAnswerName(userModel.getName());
            middlewareModel.setAnswerNoteId(noteDO.getId());
            middlewareModel.setAgree(0);
            middlewareModel.setNotAgree(0);
            middlewareModel.setSummary(noteDO.getBody().substring(0,10)+"...");
            middleService.createMiddle(middlewareModel);
        }
        noteModel.setId(noteDO.getId());
        return noteModel;
    }

    private NoteDO convertDoFromModel(NoteModel noteModel) {
        if (noteModel == null) {
            return null;
        }
        NoteDO noteDO = new NoteDO();
        BeanUtils.copyProperties(noteModel,noteDO);
        return noteDO;
    }

    @Override
    public void deleteNote(Integer noteId) {
        //校验登录
        //删除

    }

    @Override
    public List<NoteModel> showNote(String keyword, Integer userId, Integer left, Integer right) {

        //默认left - right 间隔5
        //keyword 用来做模糊查询, 标题, 或者body category
        //如果有userId 那就查询当前userid的, 并且有kerword, 模糊查询

        ArrayList<NoteModel> list = new ArrayList<>();
        if (userId == null) {

        } else {
            List<NoteDO> noteDOS = noteDOMapper.selectByUserId(userId, left, right);
            noteDOS.forEach(noteDO -> {
                list.add(covertModelFromDo(noteDO));
            });
        }
        return list;
    }

    @Override
    public NoteModel getNoteById(Integer id) {
        NoteDO noteDO = noteDOMapper.selectByPrimaryKey(id);
        List<MiddlewareModel> middlewareModels = middleService.listMiddlewareByNId(id);
        NoteModel noteModel = covertModelFromDo(noteDO);
        noteModel.setMiddleList(middlewareModels);
        return noteModel;
    }

    private NoteModel covertModelFromDo(NoteDO noteDO) {
        if (noteDO == null) {
            return null;
        }
        NoteModel noteModel = new NoteModel();
        BeanUtils.copyProperties(noteDO,noteModel);
        return noteModel;
    }
}
