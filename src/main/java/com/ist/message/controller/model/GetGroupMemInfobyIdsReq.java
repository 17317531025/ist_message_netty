package com.ist.message.controller.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class GetGroupMemInfobyIdsReq extends BaseReq{
    @NotBlank(message = "tokenId不能为空")
    @ApiModelProperty(value = "tokenId",required = true)
    private String tokenId;
    @NotBlank(message = "userId不能为空")
    @ApiModelProperty(value = "userId",required = true)
    private String userId;
    @NotBlank(message = "groupIds不能为空")
    @ApiModelProperty(value = "groupIds",required = true)
    private String groupIds;
    @ApiModelProperty(value = "talkers",required = true)
    private String talkers;
}
