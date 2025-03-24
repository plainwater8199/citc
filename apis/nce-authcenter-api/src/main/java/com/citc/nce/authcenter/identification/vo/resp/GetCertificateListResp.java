package com.citc.nce.authcenter.identification.vo.resp;

import com.citc.nce.authcenter.identification.vo.CertificateItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetCertificateListResp {
    @ApiModelProperty(value = "资质列表", dataType = "List")
    private List<CertificateItem> certificateItems;
}
