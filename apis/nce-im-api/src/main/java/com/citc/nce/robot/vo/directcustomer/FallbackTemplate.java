package com.citc.nce.robot.vo.directcustomer;

import lombok.Data;

import java.util.List;

/**
 * 文件名:FallbackTemplate
 * 创建者:zhujinyu
 * 创建时间:2024/2/28 11:30
 * 描述:
 */
@Data
public class FallbackTemplate {

    //模板ID，templateType为RCS，AIM，MMS时必选
    String templateId;

    //模版参数，动态参数模板必填
    List<Parameter> params;
}
