package com.sweprj.issue.exception;

public class InvalidIssueStateException extends RuntimeException{
    public InvalidIssueStateException(String message) {
        super(message);
    }
}
