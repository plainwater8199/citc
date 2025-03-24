package com.citc.nce.authcenter.systemmsg.vo.resp;

import com.citc.nce.authcenter.systemmsg.vo.SysMsgManageListInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class QuerySysMsgManageListResp {
    @ApiModelProperty(value = "站内信列表信息", dataType = "List")
    private List<SysMsgManageListInfo> sysMsgManageListInfos;


}
