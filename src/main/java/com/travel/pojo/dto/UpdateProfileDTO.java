package com.travel.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileDTO {
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    @Size(max = 500, message = "头像URL长度不能超过500")
    private String avatarUrl;

    @Size(max = 500, message = "简介长度不能超过500")
    private String bio;
}
