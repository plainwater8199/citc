package com.citc.nce.auth.readingLetter.template.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjy
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReadingLetterTemplateDetailVo {


    @ApiModelProperty("短信类型   1:5G阅信  2:阅信+")
    private Integer smsType;

    @ApiModelProperty("运营商编码 0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    private Integer operatorCode;

    @ApiModelProperty("送审时所使用的外部平台账号")
    private String auditAccount;

    @ApiModelProperty("审核状态:WAIT -1待审核, PENDING 1 审核中,SUCCESS 0 审核通过,FAILED  2 审核不通过")
    private Integer status;

    @ApiModelProperty("审核备注")
    private String remark;
    /**
     * 支持手机类型 英文逗号分隔
     *  HuaWei华为厂商
     * XiaoMi小米厂商
     * OPPO OPPO厂商
     * VIVO VIVO厂商
     * MEIZU魅族厂商
     */
    @ApiModelProperty("支持手机类型 英文逗号分隔")
    private String applicableTerminal;

}
