package com.citc.nce.auth.csp.smsTemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ping chen
 */
@Data
public class SmsTemplateEffectiveVo {

    @ApiModelProperty("模板ID集合")
    @NotNull(message = "模板Id集合不能为空")
    private List<String> accountIds;


    @ApiModelProperty("模板ID类型 1:普通模板;2:个性模板")
    @NotNull(message = "模板Id类型不能为空")
    private Integer templateType;

    @ApiModelProperty("模板名称")
    private String templateName;
}
