package com.citc.nce.module.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ImportContactGroupResp {
    @ApiModelProperty("导入成功人数")
    private Integer successNum;
}
