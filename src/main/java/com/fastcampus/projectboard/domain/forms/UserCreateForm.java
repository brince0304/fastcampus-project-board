package com.fastcampus.projectboard.domain.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class UserCreateForm implements Serializable {
    @Size(min = 6, max = 25, message = "* 아이디는 6자 이상 25자 이하로 입력해주세요.")
    private String userId;

    @Size(min = 8, max = 25, message = "* 패스워드는 8자 이상 25자 이하로 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,25}$", message = "* 패스워드는 영문, 숫자, 특수문자를 포함해야 합니다.")
    private String password1;

    @NotEmpty(message = "* 입력값을 확인해주세요.")
    private String password2;

    @Size(min=2, max=10, message = "* 닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String nickname;

    @NotEmpty(message = "* 이메일을 입력해주세요.")
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "* 이메일 형식을 확인해주세요.")
    private String email;

    @Size(max=50, message = "* 메모는 50자 이하로 입력해주세요.")
    private String memo;

}
