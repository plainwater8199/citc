package com.citc.nce.vo.tempStore;

import com.citc.nce.filecenter.vo.UploadResp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 扩展商城-资源管理-视频-封面资源表
 * </p>
 *
 * @author
 * @since 2024/5/11 14:27
 */
@Data
public class UpdateTid {
    //保存素材到客户侧后产生的id
    @ApiModelProperty(value = "表主键")
    private Long id;
    @ApiModelProperty(value = "文件UUID")
    private String fileUUid;
    @ApiModelProperty(value = "文件审核信息")
    private UploadResp uploadResp;
}
