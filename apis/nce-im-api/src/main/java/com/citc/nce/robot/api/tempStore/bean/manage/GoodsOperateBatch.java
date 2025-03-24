package com.citc.nce.robot.api.tempStore.bean.manage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author bydud
 * @since 2024/6/19 16:28
 */
@Data
public class GoodsOperateBatch {
    @ApiModelProperty(value = "主键id")
    @Size(min = 1, message = "mssId 不能为空")
    private List<Long> mssIdList;

    @Length(max = 100, message = "备注长度超过限制")
    @ApiModelProperty(value = "备注")
    private String remark;
}
