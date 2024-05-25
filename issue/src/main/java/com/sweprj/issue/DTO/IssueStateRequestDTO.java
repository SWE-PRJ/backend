package com.sweprj.issue.DTO;

import com.sweprj.issue.domain.enums.IssueState;
import lombok.Getter;

@Getter
public class IssueStateRequestDTO {
    private IssueState state;
}