package com.citc.nce.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年7月5日17:24:05
 * @Version: 1.0
 * @Description: IPictureService
 */
@Data
public class DeleteResp {
    @ApiModelProperty(value = "需要删除的文件urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private List<String> fileUrlIds;
}
