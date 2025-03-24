package com.citc.nce.misc.operationlog.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 操作日志记录
 * </p>
 *
 * @author bydud
 * @since 2024-01-26 11:01:21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_operation_log")
@ApiModel(value = "SysOperationLog对象", description = "操作日志记录")
public class SysOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("日志主键")
    @TableId(value = "oper_id", type = IdType.ASSIGN_ID)
    private Long operId;

    @ApiModelProperty("模块标题")
    @TableField("title")
    private String title;

    @ApiModelProperty("业务类型（0其它 1新增 2修改 3删除）")
    @TableField("business_type")
    private Integer businessType;

    @ApiModelProperty("方法名称")
    @TableField("method")
    private String method;

    @ApiModelProperty("请求方式")
    @TableField("request_method")
    private String requestMethod;

    @ApiModelProperty("操作类别（0其它 1后台用户 2手机端用户）")
    @TableField("operator_type")
    private Integer operatorType;

    @ApiModelProperty("操作人员")
    @TableField("oper_name")
    private String operName;

    @ApiModelProperty("请求URL")
    @TableField("oper_url")
    private String operUrl;

    @ApiModelProperty("主机地址")
    @TableField("oper_ip")
    private String operIp;

    @ApiModelProperty("请求参数")
    @TableField("oper_param")
    private String operParam;

    @ApiModelProperty("返回参数")
    @TableField("json_result")
    private String jsonResult;

    @ApiModelProperty("操作状态（0正常 1异常）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("错误消息")
    @TableField("error_msg")
    private String errorMsg;

    @ApiModelProperty("操作时间")
    @TableField("oper_time")
    private Date operTime;


    @ApiModelProperty("存储类型")
    @TableField(exist = false)
    private Integer storageType;
}
