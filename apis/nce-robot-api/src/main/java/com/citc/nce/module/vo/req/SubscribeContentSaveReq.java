package com.citc.nce.module.vo.req;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SubscribeContentSaveReq implements Serializable {
    private static final long serialVersionUID = -7279184138751029380L;

    @ApiModelProperty("订阅内容信息ID")
    private Long id;

    @ApiModelProperty("订阅组件表uuid")
    private String subscribeId;

    @ApiModelProperty("订阅内容表uuid")
    private String subContentId; // 订阅内容表uuid

    @ApiModelProperty("标题")
    @NotNull(message = "标题不能为空！")
    @Length(max = 100, message = "标题长度超过限制(最大100位)")
    private String title;

    @ApiModelProperty("5G消息模板id")
    @NotNull(message = "5G消息模板id不能为空！")
    private Long msg5gId;

}
