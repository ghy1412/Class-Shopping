package com.dreamone.service.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class ItemModel  implements Serializable {
    //id
    private int id;

    //商品title
    @NotBlank(message = "商品标题不能为空")
    private String title;

    //商品价格
    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "加个不能小于0")
    private BigDecimal price;

    //商品库存
    @NotNull(message = "库存不能为空")
    private Integer stock;

    //商品描述
    @NotBlank(message = "必须添加商品描述")
    private String description;

    //商品的销量
    private Integer sales;

    //商品描述图片的url
    @NotNull(message = "必须要有商品图片")
    private String imgUrl;

    //秒杀信息 使用聚合模型其实就是嵌套, 不为空则拥有还未结束的秒杀活动
    private PromoModel promoModel;

    public PromoModel getPromoModel() {
        return promoModel;
    }

    public void setPromoModel(PromoModel promoModel) {
        this.promoModel = promoModel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
