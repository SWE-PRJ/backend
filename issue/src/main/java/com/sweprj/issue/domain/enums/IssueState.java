package com.sweprj.issue.domain.enums;

public enum IssueState {
    NEW, ASSIGNED, FIXED, RESOLVED, CLOSED, REOPENED;

    public static IssueState fromString(String state) {
        try {
            return IssueState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    public static Boolean isValid(String stateString) {
        if (stateString == null) {
            return false;
        }
        try {
            IssueState.valueOf(stateString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
