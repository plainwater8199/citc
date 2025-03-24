package com.citc.nce.module.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class SignModuleUpdateReq {
    private static final long serialVersionUID = 8174725747412754986L;

    @ApiModelProperty("打卡组件信息ID")
    private Long id;

    @ApiModelProperty("打卡组件信息UUID")
    private String signModuleId;

    @ApiModelProperty("名称")
    @NotNull(message = "名称不能为空！")
    @Length(max = 25, message = "名称长度超过限制(最大25位)")
    private String name;

    @ApiModelProperty("描述")
    @NotNull(message = "打卡组件描述不能为空！")
    @Length(max = 50, message = "描述长度超过限制(最大50位)")
    private String description;

    @ApiModelProperty("参加打卡成功提示")
    private Integer joinSuccess;

    @ApiModelProperty("参加打卡成功5G消息模板id")
    private Long joinSuccess5gTmpId;

    @ApiModelProperty("打卡成功提示")
    private Integer signSuccess;

    @ApiModelProperty("打卡成功5G消息模板id")
    private Long signSuccess5gTmpId;

    @ApiModelProperty("打卡时间提示")
    private Integer signTimeWarn;

    @ApiModelProperty("打卡时间提醒5G消息模板id")
    private Long signTime5gTmpId;

    @ApiModelProperty("重复打卡提醒提示")
    private Integer signRepeatWarn;

    @ApiModelProperty("重复打卡提醒5G消息模板id")
    private Long signRepeat5gTmpId;

    @ApiModelProperty("打卡类型类型(DAY每天/FIRST(周一),SECOND(周二),THIRD(周三),FOURTH(周四),FIFTH(周五),SIXTH(周六),SEVENTH(周天))")
    @NotNull(message = "打卡时间类型不能为空！")
    private String signTimeType; //打卡类型类型(DAY每天/FIRST(周一),SECOND(周二),THIRD(周三),FOURTH(周四),FIFTH(周五),SIXTH(周六),SEVENTH(周天))

    @ApiModelProperty(value = "打卡开始时间")
    @NotNull(message = "打卡开始时间不能为空！")
    private String signStartTime;

    @ApiModelProperty(value = "打卡结束时间")
    @NotNull(message = "打卡结束时间不能为空！")
    private String signEndTime;
}
