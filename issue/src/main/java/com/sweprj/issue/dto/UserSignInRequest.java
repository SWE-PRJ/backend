package com.sweprj.issue.dto;

import com.sweprj.issue.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor //필드를 매개변수로 하는 생성자 생성
@ToString //DTO 객체가 가지고 있는 필드값 출력
public class UserSignInRequest {
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "아이디를 입력해주세요.")
    private String identifier;


    public User toEntity(String encodedPassword) {
        return User.builder()
                .name(this.name)
                .identifier(this.identifier)
                .password(encodedPassword)
                .build();
    }
}