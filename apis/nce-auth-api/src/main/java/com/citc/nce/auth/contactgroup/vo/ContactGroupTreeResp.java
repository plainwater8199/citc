package com.citc.nce.auth.contactgroup.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/10 17:19
 * @Version: 1.0
 * @Description:
 * 联系人组
 */
@Data
public class ContactGroupTreeResp implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 主键
         */
        @ApiModelProperty("id")
        private Long id;

        /**
         * 组名称
         */
        @ApiModelProperty("组名称")
        private String groupName;

    }
