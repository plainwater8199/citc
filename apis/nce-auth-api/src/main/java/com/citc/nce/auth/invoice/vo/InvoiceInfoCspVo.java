package com.citc.nce.auth.invoice.vo;

import com.citc.nce.auth.invoice.enums.InvoiceType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 发票信息管理-csp
 * </p>
 *
 * @author bydud
 * @since 2024-02-27 10:02:50
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "InvoiceInfoCspVo对象", description = "发票信息管理-csp")
public class InvoiceInfoCspVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long iicspId;

    //所属csp
    private String cspId;
    //支持类型
    private List<InvoiceType> typeList;
    //备注
    private String remark;

    private String updater;

    private Date updateTime;

}
