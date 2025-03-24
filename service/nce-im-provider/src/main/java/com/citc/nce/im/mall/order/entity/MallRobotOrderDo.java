package com.citc.nce.im.mall.order.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.entity
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  16:09
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@TableName("mall_robot_order")
public class MallRobotOrderDo {

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField(value = "order_name")
    @ApiModelProperty("指令名称")
    private String orderName;

    @TableField(value = "request_type")
    private Integer requestType;

    @TableField(value = "request_url")
    private String requestUrl;

    @TableField(value = "header_list")
    private String headerList;

    @TableField(value = "request_body_type")
    private Integer requestBodyType;

    @TableField(value = "request_raw_type")
    private Integer requestRawType;

    @TableField(value = "body_list")
    private String bodyList;

    @TableField(value = "response_type")
    private Integer responseType;

    @TableField(value = "response_list")
    private String responseList;

    @TableField(value = "deleted")
    private Integer deleted;

    @TableField(value = "deleted_time")
    private Date deletedTime;

    @TableField(value = "depiction")
    private String depiction;

    @TableField(value = "order_type")
    private String orderType;

    @TableField(value = "request_url_name")
    private String requestUrlName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private String creator;
    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;
}
