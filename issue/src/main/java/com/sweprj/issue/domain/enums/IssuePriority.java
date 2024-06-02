package com.sweprj.issue.domain.enums;

public enum IssuePriority {
    blocker, critical, major, minor, trivial;

    public static IssuePriority fromString(String priority) {
        try {
            return IssuePriority.valueOf(priority.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown priority: " + priority);
        }
    }

    public static Boolean isValid(String priorityString) {
        if (priorityString == null) {
            return false;
        }
        try {
            IssuePriority.valueOf(priorityString.toLowerCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
