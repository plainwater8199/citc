package com.citc.nce.auth.csp.videoSms.signature.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CspVideoSmsSignatureResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "账号id")
    private String accountId;

    @ApiModelProperty(value = "签名")
    private String signature;

    @ApiModelProperty(value = "类型 0:视频短信 1:其他")
    private Integer type;

    @ApiModelProperty(value = "是否删除0:未删除 1:已删除")
    private int deleted;
}
