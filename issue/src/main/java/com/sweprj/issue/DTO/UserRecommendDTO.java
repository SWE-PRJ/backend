package com.sweprj.issue.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRecommendDTO {
    private Long userId;
    private String userIdentifier;
}
