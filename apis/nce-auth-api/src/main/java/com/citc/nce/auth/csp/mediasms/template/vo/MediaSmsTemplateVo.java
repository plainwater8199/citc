package com.citc.nce.auth.csp.mediasms.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author jiancheng
 */
@Data
public class MediaSmsTemplateVo {
    private Long id;

    /*user id*/
    private String customerId;

    /*模板名称*/
    private String templateName;
}
