package com.sweprj.issue.domain.enums;

public enum IssuePriority {
    blocker, critical, major, minor, trivial;

    public static IssueState fromString(String priority) {
        try {
            return IssueState.valueOf(priority.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown priority: " + priority);
        }
    }
}
