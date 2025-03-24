package com.citc.nce.auth.csp.chatbot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChatbotSupplierUpdate extends ChatbotSupplierAdd implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "数据库主键", dataType = "Long", required = true)
    @NotNull(message = "修改id不能为空")
    private Long id;
}
