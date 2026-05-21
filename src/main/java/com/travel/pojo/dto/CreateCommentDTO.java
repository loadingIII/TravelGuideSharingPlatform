package com.travel.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentDTO {
    @NotBlank(message = "评论内容不能为空")
    private String content;
    private Long parentCommentId;
}
