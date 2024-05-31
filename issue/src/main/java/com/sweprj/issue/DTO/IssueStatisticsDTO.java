package com.sweprj.issue.DTO;


import lombok.Data;

import java.util.Map;

@Data
public class IssueStatisticsDTO {
    private long totalIssues;
    private Map<String, Long> issuesByStatus;
    private Map<String, Long> issuesByPriority;
    private Map<String, Long> issuesByMonth;
    private Map<String, Map<String, Long>> issuesByDayPerMonth;

}