package com.sweprj.issue.dto;

import com.sweprj.issue.domain.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor //필드를 매개변수로 하는 생성자 생성
@ToString //DTO 객체가 가지고 있는 필드값 출력
public class UserDTO {
    private Long id;
    private String name;
    private String password;
    private String identifier;


    public User toEntity(String encodedPassword) {
        return User.builder()
                .name(this.name)
                .identifier(this.identifier)
                .password(encodedPassword)
                .build();
    }
}