package com.citc.nce.auth.contactgroup.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/10 17:19
 * @Version: 1.0
 * @Description:
 * 联系人组
 */
@Data
@TableName("contact_group")
public class ContactGroupDo   extends BaseDo {

        private static final long serialVersionUID = 1L;

        /**
         * 组名称
         */
        @TableField(value="group_name")
        private String groupName;

        /**
         * 备注
         */
        @TableField(value="remark")
        private String remark;

        /**
         * 0未删除  1已删除
         */
        @TableField(value="deleted")
        private int deleted;

        /**
         * 删除时间
         */
        @TableField(value="delete_time")
        private Date deleteTime;

    }
