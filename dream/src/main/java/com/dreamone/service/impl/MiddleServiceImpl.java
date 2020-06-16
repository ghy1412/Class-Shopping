package com.dreamone.service.impl;

import com.dreamone.dao.MiddlewareDOMapper;
import com.dreamone.dataobject.MiddlewareDO;
import com.dreamone.service.MiddleService;
import com.dreamone.service.model.MiddlewareModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class MiddleServiceImpl implements MiddleService {

    @Autowired
    private MiddlewareDOMapper middlewareDOMapper;
    @Override
    public List<MiddlewareModel> listMiddlewareByNId(Integer noteId) {
        List<MiddlewareDO> middlewareDOS = middlewareDOMapper.selectByNoteId(noteId);
        ArrayList<MiddlewareModel> middlewareModels = new ArrayList<>();
        middlewareDOS.forEach(middlewareDO -> {
            MiddlewareModel middlewareModel = convertModelFromDo(middlewareDO);
            middlewareModels.add(middlewareModel);
        });
        return middlewareModels;
    }

    @Override
    public void createMiddle(MiddlewareModel middlewareModel) {
        MiddlewareDO middlewareDO = convertDOFromModel(middlewareModel);
        middlewareDOMapper.insertSelective(middlewareDO);
    }

    private MiddlewareDO convertDOFromModel(MiddlewareModel middlewareModel) {
        if (middlewareModel == null) {
            return null;
        }
        MiddlewareDO middlewareDO = new MiddlewareDO();
        BeanUtils.copyProperties(middlewareModel, middlewareDO);
        return middlewareDO;
    }

    private MiddlewareModel convertModelFromDo(MiddlewareDO middlewareDO) {
        if (middlewareDO == null) {
            return null;
        }
        MiddlewareModel middlewareModel = new MiddlewareModel();
        BeanUtils.copyProperties(middlewareDO, middlewareModel);
        return middlewareModel;
    }
}
