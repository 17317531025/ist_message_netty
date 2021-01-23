package com.ist.message.controller.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class SendMsgReq {
    @NotBlank(message = "message不能为空")
    private String message;
    @NotBlank(message = "receiverId不能为空")
    private String receiverId;
}
