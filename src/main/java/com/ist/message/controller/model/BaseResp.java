package com.ist.message.controller.model;

import com.ist.message.common.ResultConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Base64;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResp {
    @ApiModelProperty(value = "响应code")
    private String code;
    @ApiModelProperty(value = "响应msg")
    private String msg;

    public static BaseResp ok(){
        BaseResp resp = new BaseResp();
        resp.setCode(ResultConstant.SUCCESS_CODE);
        resp.setMsg(ResultConstant.SUCCESS_CODE_MSG);
        return resp;
    }

    public static BaseResp result(String code,String msg){
        BaseResp resp = new BaseResp();
        resp.setCode(code);
        resp.setMsg(msg);
        return resp;
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
