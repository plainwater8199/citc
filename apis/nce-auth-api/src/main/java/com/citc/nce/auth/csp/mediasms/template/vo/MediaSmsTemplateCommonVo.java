package com.citc.nce.auth.csp.mediasms.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ping chen
 */
@Data
public class MediaSmsTemplateCommonVo {

    /**
     * 模板Id集合
     */
    @ApiModelProperty("模板ID集合")
    @NotNull(message = "模板Id集合不能为空")
    private List<Long> templateIds;
}
