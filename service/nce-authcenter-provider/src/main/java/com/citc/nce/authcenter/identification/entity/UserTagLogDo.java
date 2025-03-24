package com.citc.nce.authcenter.identification.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/07/15
 * @Version 1.0
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_tag_log")
@ApiModel(value = "UserTagLog对象", description = "用户标签处理日志表")
public class UserTagLogDo extends BaseDo<UserTagLogDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户账号资质信息表id")
    private Long certificateOptionsId;

    @ApiModelProperty(value = "处理时间")
    private Date handleTime;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "管理员账号id")
    private String adminUserId;

    @ApiModelProperty(value = "管理员账号名称")
    private String adminUserName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否删除（1为已删除，0为未删除）")
    private Integer deleted;

    @ApiModelProperty(value = "未删除默认为0，删除为时间戳")
    private Long deletedTime;


}
