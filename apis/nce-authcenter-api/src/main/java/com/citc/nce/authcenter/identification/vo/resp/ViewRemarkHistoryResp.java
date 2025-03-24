package com.citc.nce.authcenter.identification.vo.resp;

import com.citc.nce.authcenter.identification.vo.IdentificationAuditItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
@Data
public class ViewRemarkHistoryResp {
    @ApiModelProperty(value = "认证审核列表")
    private List<IdentificationAuditItem> identificationAuditItems;
}
