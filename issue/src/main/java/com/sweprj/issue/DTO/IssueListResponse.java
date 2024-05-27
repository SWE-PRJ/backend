package com.sweprj.issue.DTO;

import com.sweprj.issue.domain.Issue;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class IssueListResponse {

    private Long numOfIssues;
    private List<IssueResponseDTO> issues;

    public IssueListResponse() {
        this.issues = new ArrayList<>();
        this.numOfIssues = 0L;
    }

    public void addAllIssues(List<Issue> issues) {
        for(int i = 0; i < issues.size(); i++) {
            this.add(issues.get(i));
        }
    }

    public void addAllIssuesResponse(List<IssueResponseDTO> issueResponseDTOS) {
        for(int i = 0; i < issueResponseDTOS.size(); i++) {
            this.add(issueResponseDTOS.get(i));
        }
    }

    public void add(Issue issue) {
        this.issues.add(new IssueResponseDTO(issue));
        this.numOfIssues++;
    }

    public void add(IssueResponseDTO issueResponseDTO) {
        this.issues.add(issueResponseDTO);
        this.numOfIssues++;
    }


}
