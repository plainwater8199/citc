package com.citc.nce.robot.api.mall.order.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class MallRobotOrderReq implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "指令名称", dataType = "String", required = true)
    @NotBlank(message = "指令名称不能为空")
    private String orderName;
    @ApiModelProperty(value = "请求类型 0(GET)、1(POST)", dataType = "Integer")
    private Integer requestType;
    @ApiModelProperty(value = "请求地址", dataType = "String")
    private String requestUrl;
    @ApiModelProperty(value = "请求头", dataType = "String")
    private String headerList;
    @ApiModelProperty(value = "请求数据类型 0(FORM-DATA)、1(RAW)", dataType = "Integer")
    private Integer requestBodyType;
    @ApiModelProperty(value = "请求数据类型 0(TEXT)、1(JSON)、2(XML)", dataType = "Integer")
    private Integer requestRawType;
    @ApiModelProperty(value = "请求数据", dataType = "String")
    private String bodyList;
    @ApiModelProperty(value = "响应参数类型 0(关闭)、1(打开)", dataType = "Integer")
    private Integer responseType;
    @ApiModelProperty(value = "响应参数列表", dataType = "String")
    private String responseList;
    @ApiModelProperty(value = "描述", dataType = "String")
    private String depiction;
    @ApiModelProperty(value = "请求类型 0(http)、1(自定义)", dataType = "Integer")
    private Integer orderType;
    @ApiModelProperty(value = "请求地址主体", dataType = "String")
    private String requestUrlName;
}
