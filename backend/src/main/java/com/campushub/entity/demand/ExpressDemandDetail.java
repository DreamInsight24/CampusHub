package com.campushub.entity.demand;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class ExpressDemandDetail {
    private UUID demand_uuid;
    private String pickup_location;
    private String delivery_location;
    private String pickup_code;
    private LocalDateTime expected_delivery_time;
    private ArrayList<String> image_urls;
    private String description;
    private UUID dialog_uuid;

    public UUID getDemand_uuid() {
        return demand_uuid;
    }

    public void setDemand_uuid(UUID demand_uuid) {
        this.demand_uuid = demand_uuid;
    }

    public String getPickup_location() {
        return pickup_location;
    }

    public void setPickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }

    public String getDelivery_location() {
        return delivery_location;
    }

    public void setDelivery_location(String delivery_location) {
        this.delivery_location = delivery_location;
    }

    public String getPickup_code() {
        return pickup_code;
    }

    public void setPickup_code(String pickup_code) {
        this.pickup_code = pickup_code;
    }

    public LocalDateTime getExpected_delivery_time() {
        return expected_delivery_time;
    }

    public void setExpected_delivery_time(LocalDateTime expected_delivery_time) {
        this.expected_delivery_time = expected_delivery_time;
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
