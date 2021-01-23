package com.ist.message.controller.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class CreateGroupReq extends BaseReq{
    @NotBlank(message = "optUserId不能为空")
    @ApiModelProperty(value = "操作人",required = true)
    private String optUserId;
    @NotBlank(message = "组名不能为空")
    @ApiModelProperty(value = "组名",required = true)
    private String groupName;
    @ApiModelProperty(value = "进群方式")
    private Integer joinGroup;
    @ApiModelProperty(value = "头像地址方式")
    private String avatarUrl;
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
