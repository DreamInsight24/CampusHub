package com.campushub.entity.demand;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class SecondhandDemandDetail {
    private UUID demand_uuid;
    private String item_name;
    private String category;
    private BigDecimal price;
    private BigDecimal original_price;
    private String condition_level;
    private String trade_location;
    private ArrayList<String> image_urls;
    private String description;
    private UUID dialog_uuid;

    public UUID getDemand_uuid() {
        return demand_uuid;
    }

    public void setDemand_uuid(UUID demand_uuid) {
        this.demand_uuid = demand_uuid;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(BigDecimal original_price) {
        this.original_price = original_price;
    }

    public String getCondition_level() {
        return condition_level;
    }

    public void setCondition_level(String condition_level) {
        this.condition_level = condition_level;
    }

    public String getTrade_location() {
        return trade_location;
    }

    public void setTrade_location(String trade_location) {
        this.trade_location = trade_location;
    }

    public ArrayList<String> getImage_urls() {
        return image_urls;
    }

    public void setImage_urls(ArrayList<String> image_urls) {
        this.image_urls = image_urls;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getDialog_uuid() {
        return dialog_uuid;
    }

    public void setDialog_uuid(UUID dialog_uuid) {
        this.dialog_uuid = dialog_uuid;
    }
}
