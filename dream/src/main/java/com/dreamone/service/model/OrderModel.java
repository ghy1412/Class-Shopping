package com.dreamone.service.model;

import java.math.BigDecimal;

public class OrderModel {

    //201809120001
    private String id;

    //用户id
    private Integer usesId;

    //商品id
    private Integer itemId;

    //单价
    private BigDecimal itemPrice;

    //商品数量
    private Integer amount;

    //商品金额
    private BigDecimal orderPrice;

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public OrderModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUsesId() {
        return usesId;
    }

    public void setUsesId(Integer usesId) {
        this.usesId = usesId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }
}
