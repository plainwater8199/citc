package com.citc.nce.aim.privatenumber.vo.req;

import com.citc.nce.authcenter.captcha.vo.req.CaptchaCheckReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PrivateNumberProjectTestReq {
    @NotNull(message = "验证码信息不能为空")
    @ApiModelProperty(value = "验证码信息", dataType = "Object", required = true)
    private CaptchaCheckReq captchaInfo;

    @NotBlank(message = "项目ID不能为空")
    @ApiModelProperty(value = "项目id", dataType = "String", required = true)
    private String projectId;

    @NotNull(message = "手机号列表不能为空")
    @ApiModelProperty(value = "手机号", dataType = "String", required = true)
    private List<String> phones;
}
