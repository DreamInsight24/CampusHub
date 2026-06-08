package com.campushub.common.enums;

public enum Score {
    HANG(5),
    DINGJ(4),
    RSR(3),
    NPC(2),
    LA(1);
    //夯
    private final int score;
    Score(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
