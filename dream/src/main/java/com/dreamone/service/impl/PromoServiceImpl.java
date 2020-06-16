package com.dreamone.service.impl;

import com.dreamone.dao.PromoDOMapper;
import com.dreamone.dataobject.PromoDO;
import com.dreamone.service.PromoService;
import com.dreamone.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PromoServiceImpl implements PromoService {


    @Autowired
    private PromoDOMapper promoDOMapper;
    @Override
    public PromoModel getPromoModelByItemId(Integer id) {
        System.out.println("到达活动");
        System.out.println(id);

        PromoDO promoModelByItemId = promoDOMapper.selectByItemId(id);

        if (promoModelByItemId == null) {
            return null;
        }
        System.out.println(promoModelByItemId.getPromoItemPrice());
        System.out.println(promoModelByItemId.getStartDate());
        System.out.println("没有转换前的时间:"+promoModelByItemId.getEndDate());
        PromoModel promoModel = convertModelFromDO(promoModelByItemId);
        if (promoModel == null) {
            return null;
        }
        System.out.println(promoModel.getEndDate());
        System.out.println(promoModel.getEndDate().isBeforeNow());
        System.out.println(DateTime.now());
        //判断当前时间是否秒杀活动即将开始或者正在进行
        System.out.println("现在");
        if (promoModel.getStartDate().isAfterNow()) {
            System.out.println(promoModel.getStartDate());
            promoModel.setStatus(1);
        } else if (promoModel.getEndDate().isBeforeNow()) {
            System.out.println(promoModel.getEndDate());
            promoModel.setStatus(3);
            System.out.println(3);
        } else {
            System.out.println("活动正在进行中");
            promoModel.setStatus(2);
        }
        return promoModel;
    }

    private PromoModel convertModelFromDO(PromoDO promoDO) {
        if (promoDO == null) {
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO, promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }
}
