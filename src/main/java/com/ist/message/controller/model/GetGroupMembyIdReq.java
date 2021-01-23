package com.ist.message.controller.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class GetGroupMembyIdReq {
    @NotBlank(message = "tokenId不能为空")
    @ApiModelProperty(value = "tokenId",required = true)
    private String tokenId;
    @NotBlank(message = "userId不能为空")
    @ApiModelProperty(value = "userId",required = true)
    private String userId;
    @NotBlank(message = "groupId不能为空")
    @ApiModelProperty(value = "groupId",required = true)
    private String groupId;
}
