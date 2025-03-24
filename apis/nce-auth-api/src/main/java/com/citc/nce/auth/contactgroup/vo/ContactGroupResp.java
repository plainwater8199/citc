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
public class ContactGroupResp implements Serializable {

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

        /**
         * 备注
         */
        @ApiModelProperty("备注")
        private String remark;

        /**
         * 人员数量
         */
        @ApiModelProperty("人员数量")
        private Long personNum;

        /**
         * 创建者
         */
        @ApiModelProperty("创建者")
        private String creator;

        /**
         * 创建时间
         */
        @ApiModelProperty("创建时间")
        private Date createTime;

        /**
         * 更新者
         */
        @ApiModelProperty("更新者")
        private String updater;

        /**
         * 更新时间
         */
        @ApiModelProperty("更新时间")
        private Date updateTime;

    }
