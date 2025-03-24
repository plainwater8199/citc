package com.citc.nce.authcenter.auth.vo.req;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * @Author: zhujy
 * @Date: 2024/3/17 14:34
 * @Version: 1.0
 * @Description: 驳回CSP的chaotbot申请
 */
@Data
@Accessors(chain = true)
public class RejectSupplierChatbotConfigurationReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * chatbot 的uuid
     */
    private String chatbotAccountId;

    /**
     * 备注
     */
    private String remark;

}
