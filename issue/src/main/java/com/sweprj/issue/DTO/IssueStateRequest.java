package com.sweprj.issue.DTO;

import com.sweprj.issue.domain.enums.IssueState;
import lombok.Getter;

@Getter
public class IssueStateRequest {
    private String state;
}