package com.CH.security.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;


@Data
public class LoginFromDto {
    @NotBlank(message = "用户账户不可为空")
    private String userAccount;
    @NotBlank(message = "用户密码不可为空")
    private String userPwd;
    private String code;
}
