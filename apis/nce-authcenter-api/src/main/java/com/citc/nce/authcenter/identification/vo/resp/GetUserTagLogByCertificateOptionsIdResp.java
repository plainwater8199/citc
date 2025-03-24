package com.citc.nce.authcenter.identification.vo.resp;

import com.citc.nce.authcenter.identification.vo.UserTagLogItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
@Data
public class GetUserTagLogByCertificateOptionsIdResp {
    @ApiModelProperty(value = "用户标签记录列表")
    private List<UserTagLogItem> userTagLogItems;
}
