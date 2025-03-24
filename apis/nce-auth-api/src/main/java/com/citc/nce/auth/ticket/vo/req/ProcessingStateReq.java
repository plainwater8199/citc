package com.citc.nce.auth.ticket.vo.req;

import com.citc.nce.auth.certificate.vo.req.DisposeCertificateOptionsReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: zouyili
 * @Contact: ylzouf
 * @Date: 2022/6/21 17:06
 * @Version: 1.0
 * @Description:
 */
@Data
public class ProcessingStateReq extends DisposeCertificateOptionsReq implements Serializable {

    @NotNull(message = "主键id不能为空")
    @ApiModelProperty(value = "表id", dataType = "Long", required = true)
    private Long id;

    @Length(max = 200, message = "备注长度超过限制")
    @ApiModelProperty(value = "备注", dataType = "String", required = false)
    private String remark;

    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    private String userId;

}
