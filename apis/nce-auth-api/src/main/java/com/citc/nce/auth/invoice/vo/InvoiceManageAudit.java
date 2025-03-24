package com.citc.nce.auth.invoice.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 2024/3/7
 */
@Getter
@Setter
@Accessors(chain = true)
public class InvoiceManageAudit {
    @NotNull(message = "imId不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imId;
    @NotNull(message = "审核状态结果")
    private boolean pass;
    @Length(max = 200, message = "驳回原因超过长度限制")
    private String remark;
}
