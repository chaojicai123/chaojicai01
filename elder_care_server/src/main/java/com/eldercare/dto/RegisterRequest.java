package com.eldercare.dto;

import com.eldercare.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100)
    private String password;

    @Size(max = 50)
    private String realName;

    @Size(max = 20)
    private String phone;

    /** 注册时仅允许 STAFF 或 FAMILY，不允许 ADMIN */
    private User.Role role = User.Role.FAMILY;
}
