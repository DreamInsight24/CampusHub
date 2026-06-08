package com.campushub.dto.demand;

import java.math.BigDecimal;
import java.util.List;

import com.campushub.common.enums.DemandType;

public class DemandCreateDTO {
    private DemandType type;
    private String title;
    private String description;
    private String location;
    private String deadline;
    private List<String> imageUrls;

    private String pickupLocation;
    private String deliveryLocation;
    private String pickupCode;
    private String expectedDeliveryTime;

    private String itemName;
    private String category;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String conditionLevel;
    private String tradeLocation;

    private String subject;
    private String tutoringMode;
    private String expectedTime;
    private Integer duration;
    private String levelRequirement;

    private String teamGoal;
    private Integer currentMembers;
    private Integer expectedMembers;
    private List<String> requiredSkills;
    private String contactMethod;

    public DemandType getType() {
        return type;
    }

    public void setType(DemandType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }

    public String getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(String expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getConditionLevel() {
        return conditionLevel;
    }

    public void setConditionLevel(String conditionLevel) {
        this.conditionLevel = conditionLevel;
    }

    public String getTradeLocation() {
        return tradeLocation;
    }

    public void setTradeLocation(String tradeLocation) {
        this.tradeLocation = tradeLocation;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTutoringMode() {
        return tutoringMode;
    }

    public void setTutoringMode(String tutoringMode) {
        this.tutoringMode = tutoringMode;
    }

    public String getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(String expectedTime) {
        this.expectedTime = expectedTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getLevelRequirement() {
        return levelRequirement;
    }

    public void setLevelRequirement(String levelRequirement) {
        this.levelRequirement = levelRequirement;
    }

    public String getTeamGoal() {
        return teamGoal;
    }

    public void setTeamGoal(String teamGoal) {
        this.teamGoal = teamGoal;
    }

    public Integer getCurrentMembers() {
        return currentMembers;
    }

    public void setCurrentMembers(Integer currentMembers) {
        this.currentMembers = currentMembers;
    }

    public Integer getExpectedMembers() {
        return expectedMembers;
    }

    public void setExpectedMembers(Integer expectedMembers) {
        this.expectedMembers = expectedMembers;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getContactMethod() {
        return contactMethod;
    }

    public void setContactMethod(String contactMethod) {
        this.contactMethod = contactMethod;
    }
}
