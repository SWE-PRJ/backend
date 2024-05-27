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
    private List<IssueResponse> issues;

    public IssueListResponse() {
        this.issues = new ArrayList<>();
        this.numOfIssues = 0L;
    }

    public void addAllIssues(List<Issue> issues) {
        for(int i = 0; i < issues.size(); i++) {
            this.add(issues.get(i));
        }
    }

    public void addAllIssuesResponse(List<IssueResponse> issueResponses) {
        for(int i = 0; i < issueResponses.size(); i++) {
            this.add(issueResponses.get(i));
        }
    }

    public void add(Issue issue) {
        this.issues.add(new IssueResponse(issue));
        this.numOfIssues++;
    }

    public void add(IssueResponse issueResponse) {
        this.issues.add(issueResponse);
        this.numOfIssues++;
    }


}
