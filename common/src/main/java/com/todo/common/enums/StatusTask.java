package com.todo.common.enums;

public enum StatusTask {
    ACTIVE("Активна"),
    COMPLETED ("Завершена");

    private final String displayName;

    StatusTask(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}