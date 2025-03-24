package com.citc.nce.auth.csp.videoSms.account.dto;

import com.citc.nce.common.core.pojo.PageParam;
import lombok.Data;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 15:10
 */
@Data
public class CspVideoSmsAccountDto extends PageParam {

    private String name;

    private String dictCode;

    private Integer status;

    private Integer currentPage;

    private String customerId;

    private String cspId;
}
