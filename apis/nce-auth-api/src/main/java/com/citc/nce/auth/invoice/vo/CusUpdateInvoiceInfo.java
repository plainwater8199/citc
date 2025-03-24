package com.citc.nce.auth.invoice.vo;

import com.citc.nce.auth.invoice.enums.InvoiceType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author bydud
 * @since 2024-02-27 10:02:50
 */
@Getter
@Setter
@Accessors(chain = true)
public class CusUpdateInvoiceInfo implements Serializable {

    private static final long serialVersionUID = 1L;


    private String customerId;

    @Length(min = 1, message = "发票类型不能为空")
    private List<InvoiceType> typeList;

    @NotEmpty(message = "说明不能为空")
    private String remark;

}
