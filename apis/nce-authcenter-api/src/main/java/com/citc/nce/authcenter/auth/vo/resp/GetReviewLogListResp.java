package com.citc.nce.authcenter.auth.vo.resp;

import com.citc.nce.authcenter.identification.vo.ApprovalLogItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetReviewLogListResp {
    @ApiModelProperty(value = "审核记录列表")
    private List<ApprovalLogItem> approvalLogItems;
}
