package com.citc.nce.authcenter.systemmsg.vo.resp;

import com.citc.nce.authcenter.systemmsg.vo.SysMsgManageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class QuerySysMsgManageDetailResp extends SysMsgManageInfo {
    @ApiModelProperty(value = "创建人", dataType = "String")
    private String creator;
    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;
    @ApiModelProperty(value = "对象接受信息", dataType = "Map")
    private Map<String,String> receiveObjectMap;
}
