package com.citc.nce.authcenter.auth.vo.resp;

import com.citc.nce.authcenter.auth.vo.CspCustomerChatbotAccountVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CspCustomerChatbotAccountListResp {
    @ApiModelProperty(value = "用户id")
    private List<CspCustomerChatbotAccountVo> cspCustomerChatbotAccountVos;
}
