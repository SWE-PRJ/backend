package com.sweprj.issue.DTO;


import lombok.Data;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@Data
public class IssueStatisticsDTO {
    private long totalIssues;
    private Map<String, Long> issuesByStatus;
    private Map<String, Long> issuesByPriority;
    private Map<String, Long> issuesByMonth;
    private Map<String, Map<String, Long>> issuesByDayPerMonth;

    public String toTable(){
        StringBuilder sb = new StringBuilder();
        sb.append("Total Issues:\t").append(totalIssues).append("\n\n");
        sb.append("Issues By Status:\n");
        sb.append(String.format("%-20s %-10s\n", "State", "Count"));
        sb.append("-------------------------------\n");
        new TreeMap<>(issuesByStatus).forEach((status, count) -> sb.append(String.format("%-20s %-10d\n\n", status, count)));

        sb.append("Issues By Priority:\n");

        sb.append(String.format("%-20s %-10s\n", "Priority", "Count"));
        sb.append("-------------------------------\n");
        new TreeMap<>(issuesByPriority).forEach((priority, count) -> sb.append(String.format("%-20s %-10d\n\n", priority, count)));

        sb.append("Issues By Month:\n");
        sb.append(String.format("%-20s %-10s\n", "Month", "Count"));
        sb.append("-------------------------------\n");
        new TreeMap<>(issuesByMonth).entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> sb.append(String.format("%-20s %-10d\n\n", entry.getKey(), entry.getValue())));

        sb.append("Issues By Day Per Month:\n");
        issuesByDayPerMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    sb.append(entry.getKey()).append("\n");
                    sb.append(String.format("%-10s %-10s\n", "Day", "Count"));
                    sb.append("-------------------------------\n");
                    entry.getValue().entrySet().stream()
                            .sorted(Map.Entry.comparingByKey(Comparator.comparingInt(Integer::parseInt)))
                            .forEach(dayEntry -> sb.append(String.format("%-10s %-10d\n", dayEntry.getKey(), dayEntry.getValue())));
                    sb.append("\n");
                });

        return sb.toString();
    }
}