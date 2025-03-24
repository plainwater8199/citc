package com.citc.nce.module.vo.req;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SignModuleReq extends PageParam implements Serializable {

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

    @ApiModelProperty("打卡参加人数.")
    private Long signCount;

    @ApiModelProperty("打卡时间：打卡类型类型(DAY每天/First,second,third,fourth,fifth,sixth,seventh(每周第几天)/CUSTOM(自定义)")
    private String signTimeType; //打卡类型类型(DAY每天/First,second,third,fourth,fifth,sixth,seventh(每周第几天)/CUSTOM(自定义))

    @ApiModelProperty(value = "打卡开始时间")
    private String signStartTime;

    @ApiModelProperty(value = "打卡结束时间")
    private String signEndTime;

}
