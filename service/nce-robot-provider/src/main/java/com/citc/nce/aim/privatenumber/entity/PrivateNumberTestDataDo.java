package com.citc.nce.aim.privatenumber.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("aim_private_number_test_data")
public class PrivateNumberTestDataDo extends BaseDo<PrivateNumberTestDataDo> {

    @ApiModelProperty("客户appKey")
    private String appKey;

    @ApiModelProperty("接受方手机号")
    private String called;

    @ApiModelProperty("交互状态")
    private Integer status;

    @ApiModelProperty("交互结果")
    private String response;

    /**
     * 是否删除
     * 0:未删除 1:已删除
     */
    private int deleted;
    /**
     * 删除时间
     */
    private long deletedTime;

    private String creatorOld;

    private String updaterOld;

}
