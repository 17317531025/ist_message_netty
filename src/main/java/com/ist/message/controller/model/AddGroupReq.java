package com.ist.message.controller.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class AddGroupReq {
    @NotBlank(message = "群主id(groupId)不能为空")
    @ApiModelProperty(value = "群主id",required = true)
    private String groupId;
    @NotBlank(message = "被邀请者(talker)为空")
    @ApiModelProperty(value = "被邀请者",required = true)
    private String talker;
    @NotBlank(message = "邀请者(inviteUserId)为空")
    @ApiModelProperty(value = "邀请者",required = true)
    private String inviteUserId;
    private Short role;
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
