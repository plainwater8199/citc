package com.citc.nce.auth.contactgroup.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/10 17:19
 * @Version: 1.0
 * @Description:
 * 联系人组
 */
@Data
public class ContactGroupReq implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 组名称
         */
        @ApiModelProperty("组名称")
        @NotBlank(message = "组名称不能为空")
        private String groupName;

        /**
         * 备注
         */
        @ApiModelProperty("备注")
        private String remark;

    }
