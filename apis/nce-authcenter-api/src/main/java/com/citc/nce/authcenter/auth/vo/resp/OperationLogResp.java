package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: zhujy
 * @Date: 2024/3/16 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class OperationLogResp implements Serializable {

    /**
     * 用户id
     */
    private String userName;

    /**
     * 内部所用机器人id,是UUID, 非运营商/供应商提供的chatbotAccount
     */
    private String chatbotAccountId;

    /**
     * 处理内容  @See com.citc.nce.authcenter.directCustomer.enums.OperationTypeEnum
     */
    private int operationTypeCode;
    private String operationType;
    /**
     * 备注
     */
    private String remark;

    /**
     * 删除时间戳
     */
    private Date createTime;

    private String creator;

}