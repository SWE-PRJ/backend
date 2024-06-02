package com.sweprj.issue.DTO;

import com.sweprj.issue.domain.Project;
import com.sweprj.issue.domain.ProjectUser;
import com.sweprj.issue.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProjectUsersResponse {
    private Long id;
    private String name;
    private List<UserResponse> userList;

    public ProjectUsersResponse(Project project, List<ProjectUser> projectUsers) {
        this.id = project.getId();
        this.name = project.getName();
        this.userList = new ArrayList<>();
        for(int i = 0; i < projectUsers.size();i++) {
            userList.add(new UserResponse(projectUsers.get(i).getUser()));
        }
    }

    @Getter
    @Setter
    public class UserResponse {
        private String role;
        private String identifier;

        UserResponse(User user) {
            this.role = user.getRole();
            this.identifier = user.getIdentifier();
        }
    }
}
