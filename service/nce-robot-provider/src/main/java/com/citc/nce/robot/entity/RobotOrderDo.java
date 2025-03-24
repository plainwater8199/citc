package com.citc.nce.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.entity
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  16:09
 
 * @Version: 1.0
 */
@Data
@TableName("robot_order")
@Accessors(chain = true)
public class RobotOrderDo extends BaseDo<RobotOrderDo> {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(value = "chatbot_account_id")
    private String chatbotAccountId;

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

    //描述
    @TableField(value = "depiction")
    private String depiction;
    //指令类型
    @TableField(value = "order_type")
    private String orderType;
    //请求地址主体
    @TableField(value = "request_url_name")
    private String requestUrlName;
    // 扩展商城订单Id
    @TableField(value = "ts_order_id")
    private Long tsOrderId;

}
