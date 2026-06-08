package com.campushub.entity.demand;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class TeamupDemandDetail {
    private UUID demand_uuid;
    private String team_goal;
    private Integer current_members;
    private Integer expected_members;
    private ArrayList<String> required_skills;
    private LocalDateTime deadline;
    private String contact_method;
    private ArrayList<String> image_urls;
    private String description;
    private UUID dialog_uuid;

    public UUID getDemand_uuid() {
        return demand_uuid;
    }

    public void setDemand_uuid(UUID demand_uuid) {
        this.demand_uuid = demand_uuid;
    }

    public String getTeam_goal() {
        return team_goal;
    }

    public void setTeam_goal(String team_goal) {
        this.team_goal = team_goal;
    }

    public Integer getCurrent_members() {
        return current_members;
    }

    public void setCurrent_members(Integer current_members) {
        this.current_members = current_members;
    }

    public Integer getExpected_members() {
        return expected_members;
    }

    public void setExpected_members(Integer expected_members) {
        this.expected_members = expected_members;
    }

    public ArrayList<String> getRequired_skills() {
        return required_skills;
    }

    public void setRequired_skills(ArrayList<String> required_skills) {
        this.required_skills = required_skills;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getContact_method() {
        return contact_method;
    }

    public void setContact_method(String contact_method) {
        this.contact_method = contact_method;
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
