package com.citc.nce.robot.api.materialSquare.vo.summary;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.robot.api.materialSquare.emums.MsAuditStatus;
import com.citc.nce.robot.api.materialSquare.emums.MsSource;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.materialSquare.emums.MsWorksLibraryStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 作品库查询列表
 *
 * @author bydud
 * @since 2024/6/21 11:02
 */
@Data
public class MsWlPage extends PageParam {
    @ApiModelProperty("类型")
    private MsType msType;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("来源")
    private MsSource msSource;
//    @ApiModelProperty("cspId")
//    private List<String> cspId;

    @ApiModelProperty("发布者")
    private String creator;

    @ApiModelProperty("作品库状态")
    private MsWorksLibraryStatus worksLibraryStatus;
    @ApiModelProperty("状态:-1:全部,0:待审核,1:审核不通过,2:已上架,3:已下架")
    private MsAuditStatus status;
}
