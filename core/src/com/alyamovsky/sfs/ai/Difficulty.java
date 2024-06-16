package com.alyamovsky.sfs.ai;

public enum Difficulty {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    private final String name;

    Difficulty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public float getModifier() {
        switch (this) {
            case EASY:
                return 0.5f;
            case MEDIUM:
                return 0.6f;
            case HARD:
                return 0.7f;
        }
        return 0;
    }
}
