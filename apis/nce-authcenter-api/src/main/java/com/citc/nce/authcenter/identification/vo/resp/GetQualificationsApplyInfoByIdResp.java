package com.citc.nce.authcenter.identification.vo.resp;

import com.citc.nce.authcenter.identification.vo.CertificateOptionsResp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: zouyili
 * @Contact: ylzouf
 * @Date: 2022/6/21 17:06
 * @Version: 1.0
 * @Description:
 */
@Data
public class GetQualificationsApplyInfoByIdResp implements Serializable {

    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "资质名称", dataType = "Integer")
    private Integer qualificationName;

    @ApiModelProperty(value = "处理状态", dataType = "Integer")
    private Integer processingState;

    @ApiModelProperty(value = "用户名", dataType = "String")
    private String userName;

    @ApiModelProperty(value = "备注", dataType = "String")
    private String remark;

    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "联系邮箱", dataType = "String")
    private String email;

    @ApiModelProperty(value = "提交时间", dataType = "Date")
    private Date submitTime;

    @ApiModelProperty(value = "创建者", dataType = "String")
    private String creator;

    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

    @ApiModelProperty(value = "更新者", dataType = "String")
    private String updater;

    @ApiModelProperty(value = "更新时间", dataType = "Date")
    private Date updateTime;

    @ApiModelProperty(value = "是否删除（1为已删除，0为未删除）", dataType = "Integer")
    private Integer deleted;

    @ApiModelProperty(value = "未删除默认为0，删除为时间戳", dataType = "Long")
    private Long deletedTime;

    private CertificateOptionsResp certificateOptionsResp;

}
