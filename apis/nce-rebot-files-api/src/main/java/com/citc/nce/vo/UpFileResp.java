package com.citc.nce.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class UpFileResp implements Serializable {

    private static final long serialVersionUID = 9106172464104261562L;

    @ApiModelProperty(value = "文件id",example = "2")
    private Long id;

    @ApiModelProperty(value = "文件名称",example = "a.pdf")
    private String fileName;

    @ApiModelProperty(value = "文件格式",example = "pdf")
    private String fileFormat;

    @ApiModelProperty(value = "文件urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String fileUrlId;

    @ApiModelProperty(value = "文件大小",example = "1.2M")
    private String fileSize;

    @ApiModelProperty(value = "文件上传时间",example = "2022-07-05")
    private Date fileUploadTime;

    @ApiModelProperty(value = "账户集合")
    private List<AccountResp> accounts;

    @ApiModelProperty(value = "是否被引用")
    private Integer used;
}
