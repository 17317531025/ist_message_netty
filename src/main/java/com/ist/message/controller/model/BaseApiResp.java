package com.ist.message.controller.model;

import com.ist.message.common.ResultConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Setter
@Getter
public class BaseApiResp {
    @ApiModelProperty(value = "响应code")
    private String code;
    @ApiModelProperty(value = "响应msg")
    private String msg;
    @ApiModelProperty(value = "响应info")
    private Object info;
    public static BaseApiResp succ(Object info){
        BaseApiResp resp = new BaseApiResp();
        resp.setInfo(info);
        resp.setCode(ResultConstant.SUCCESS_CODE);
        resp.setMsg(ResultConstant.SUCCESS_CODE_MSG);
        return resp;
    }
    public static BaseApiResp result(String code,String msg){
        BaseApiResp resp = new BaseApiResp();
        resp.setCode(code);
        resp.setMsg(msg);
        return resp;
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
