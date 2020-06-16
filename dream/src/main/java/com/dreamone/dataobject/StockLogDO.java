package com.dreamone.dataobject;

import java.io.Serializable;

public class StockLogDO implements Serializable {
    private String stockLogId;

    private Integer itemId;

    private Integer amount;

    private Integer status;

    private static final long serialVersionUID = 1L;

    public String getStockLogId() {
        return stockLogId;
    }

    public void setStockLogId(String stockLogId) {
        this.stockLogId = stockLogId == null ? null : stockLogId.trim();
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}