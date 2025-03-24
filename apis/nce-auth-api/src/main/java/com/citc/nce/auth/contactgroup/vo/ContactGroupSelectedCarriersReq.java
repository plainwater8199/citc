package com.citc.nce.auth.contactgroup.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: zjy
 * @Date: 2024/8/6 17:19
 * @Version: 1.0
 * @Description:
 * 联系人组
 */
@Data
public class ContactGroupSelectedCarriersReq implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 组名称
         */
        @ApiModelProperty("联系人组")
        @NotNull(message = "联系人组id不能为空")
        private Long contactId;

    }
