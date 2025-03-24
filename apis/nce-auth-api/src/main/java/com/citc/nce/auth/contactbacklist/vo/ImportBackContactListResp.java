package com.citc.nce.auth.contactbacklist.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 14:34
 * @Version: 1.0
 * @Description:
 */
@Data
public class ImportBackContactListResp implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 成功数量
     */
    @ApiModelProperty("成功数量")
    private Integer successNum;

    /**
     * 联系人组id
     */
    @ApiModelProperty("失败数量")
    private Integer errorNum;

}
