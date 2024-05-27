package com.sweprj.issue.DTO;

import com.sweprj.issue.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLogInRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String identifier;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

//    public User toEntity(String encodedPassword) {
//        return User.builder()
//                .identifier(this.identifier)
//                .password(encodedPassword)
//                .build();
//    }

}