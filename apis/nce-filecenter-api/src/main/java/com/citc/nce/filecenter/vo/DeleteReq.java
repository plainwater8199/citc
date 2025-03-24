package com.citc.nce.filecenter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年7月5日17:49:43
 * @Version: 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DeleteReq {

    @NotNull
    @ApiModelProperty(value = "需要删除的文件urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private List<String> fileUrlIds;
}
