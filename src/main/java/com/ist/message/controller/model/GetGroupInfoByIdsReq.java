package com.ist.message.controller.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class GetGroupInfoByIdsReq extends BaseReq{
    @NotBlank(message = "groupIds不能为空")
    @ApiModelProperty(value = "分组id(多个以逗号分开)",required = true)
    private String groupIds;
    @NotBlank(message = "tokenId不能为空")
    @ApiModelProperty(value = "tokenId",required = true)
    private String tokenId;
    @NotBlank(message = "userId不能为空")
    @ApiModelProperty(value = "userId",required = true)
    private String userId;
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
