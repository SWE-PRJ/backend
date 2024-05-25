package com.sweprj.issue.domain.enums;

public enum IssueState {
    NEW, ASSIGNED, RESOLVED, CLOSED, REOPENED;


    public static IssueState fromString(String state) {
        try {
            return IssueState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }
}
