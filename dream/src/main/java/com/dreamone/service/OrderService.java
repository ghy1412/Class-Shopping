package com.dreamone.service;

import com.dreamone.error.BusinessException;
import com.dreamone.service.model.OrderModel;

public interface OrderService {
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId) throws BusinessException;
}
