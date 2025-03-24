package com.citc.nce.dto;

import com.citc.nce.filecenter.vo.TemplateOwnershipReflect;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Accessors(chain = true)
public class VerificationReq {
    @NotEmpty
    @ApiModelProperty(value = "素材UUID",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private List<String> fileIds;

    @NotEmpty
    @ApiModelProperty(value = "运营商集合",example = "联通")
    private String operators;
    @NotEmpty
    @ApiModelProperty(value = "运营商和服务商集合",example = "运营商和服务商集合")
    private List<TemplateOwnershipReflect> ownershipReflect;

    private Integer days;
    @ApiModelProperty(value = "chatbot账号集合",example = "chatbot账号集合")
    private String  accounts;
    @ApiModelProperty(value = "creator",example = "creator")
    String creator;
    /**
     * 要验证素材的模板来源  1 群发计划 有 通过所有账号审核的素材，则可以送审，2 机器人流程 需要所有素材通过所有账号审核
     */
    Integer templateSource;
}
