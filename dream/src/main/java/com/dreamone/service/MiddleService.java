package com.dreamone.service;

import com.dreamone.service.model.MiddlewareModel;

import java.util.List;

public interface MiddleService {
    List<MiddlewareModel> listMiddlewareByNId(Integer noteId);

    void createMiddle(MiddlewareModel middlewareModel);
}
