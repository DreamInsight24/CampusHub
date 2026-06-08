package com.campushub.entity.demand;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class TutoringDemandDetail {
    private UUID demand_uuid;
    private String subject;
    private String tutoring_mode;
    private LocalDateTime expected_time;
    private Integer duration;
    private String level_requirement;
    private ArrayList<String> image_urls;
    private String description;
    private UUID dialog_uuid;

    public UUID getDemand_uuid() {
        return demand_uuid;
    }

    public void setDemand_uuid(UUID demand_uuid) {
        this.demand_uuid = demand_uuid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTutoring_mode() {
        return tutoring_mode;
    }

    public void setTutoring_mode(String tutoring_mode) {
        this.tutoring_mode = tutoring_mode;
    }

    public LocalDateTime getExpected_time() {
        return expected_time;
    }

    public void setExpected_time(LocalDateTime expected_time) {
        this.expected_time = expected_time;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getLevel_requirement() {
        return level_requirement;
    }

    public void setLevel_requirement(String level_requirement) {
        this.level_requirement = level_requirement;
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
