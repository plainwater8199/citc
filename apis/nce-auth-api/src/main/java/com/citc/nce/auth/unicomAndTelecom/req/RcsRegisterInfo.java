package com.citc.nce.auth.unicomAndTelecom.req;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RcsRegisterInfo {

    /**
     * 客户名称
     */
    private String ecName;
    /**
     * 客户行业类型 id
     */
    private Integer businessType;
    /**
     * 客户登记
     */
    private Integer ecGrade;
    private String cspEcNo;
}
