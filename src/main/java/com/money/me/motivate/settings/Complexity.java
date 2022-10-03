package com.money.me.motivate.settings;

public enum Complexity {
    EASY(1),
    MEDIUM(2),
    HARD(3),
    VERY_HARD(5);

    private final double taskModifier;

    Complexity(double taskModifier) {
        this.taskModifier = taskModifier;
    }

    public double getTaskModifier() {
        return taskModifier;
    }
}
