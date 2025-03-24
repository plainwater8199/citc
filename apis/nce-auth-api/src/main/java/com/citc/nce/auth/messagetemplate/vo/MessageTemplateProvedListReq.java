package com.citc.nce.auth.messagetemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: zhujy
 * @Date: 2024/3/19 16:23
 * @Version: 1.0
 * @Description:
 */
@Data
public class MessageTemplateProvedListReq implements Serializable {

    private static final long serialVersionUID = 1L;

    List<MessageTemplateProvedReq> templateProvedReqs;
}
