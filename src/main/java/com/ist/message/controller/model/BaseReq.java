package com.ist.message.controller.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseReq {
    @NotBlank(message = "appId不能为空")
    @ApiModelProperty(value = "秘钥id",required = true)
    private String appId;
    @NotBlank(message = "time不能为空")
    @ApiModelProperty(value = "时间戳",required = true)
    private String time;
    @ApiModelProperty(value = "签名后的串",required = true)
    @NotBlank(message = "sign不能为空")
    private String sign;
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
