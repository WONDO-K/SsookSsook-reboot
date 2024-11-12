package com.stillalive.Ssook_BE.enums;

public enum Activity {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    VERY_HIGH(4);

    private final int level;

    Activity(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
