package com.campushub.entity;

import com.campushub.common.enums.Score;

import java.util.UUID;

public class Feedback {
    private UUID demand_id;
    private UUID owner_uuid;
    private UUID commentor_uuid;
    private Boolean is_anonymous;
    private String comment;
    private Score score;


    public UUID getDemandId() {
        return demand_id;
    }

    public void setDemandId(UUID demand_id) {
        this.demand_id = demand_id;
    }

    public UUID getOwnerUuid() {
        return owner_uuid;
    }

    public void setOwnerUuid(UUID owner_uuid) {
        this.owner_uuid = owner_uuid;
    }

    public UUID getCommentorUuid() {
        return commentor_uuid;
    }

    public void setCommentorUuid(UUID commentor_uuid) {
        this.commentor_uuid = commentor_uuid;
    }

    public Boolean isAnonymous() {
        return is_anonymous;
    }

    public void setAnonymous(Boolean is_anonymous) {
        this.is_anonymous = is_anonymous;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }
}
