package com.citc.nce.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: IPictureService
 */
@Data
public class AudioResp implements Serializable {

    private static final long serialVersionUID = -3025532977874902327L;

    @ApiModelProperty(value = "音频id",example = "2")
    private Long id;

    @ApiModelProperty(value = "音频名称",example = "a.mp3")
    private String audioName;

    @ApiModelProperty(value = "音频时长",example = "1分20秒")
    private String audioDuration;

    @ApiModelProperty(value = "音频大小",example = "2M")
    private String audioSize;

    @ApiModelProperty(value = "音频urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String audioUrlId;

    @ApiModelProperty(value = "音频上传时间",example = "2022-07-05")
    private Date audioUploadTime;

    @ApiModelProperty(value = "账户集合")
    private List<AccountResp> accounts;

    @ApiModelProperty(value = "是否被引用")
    private Integer used;
}
