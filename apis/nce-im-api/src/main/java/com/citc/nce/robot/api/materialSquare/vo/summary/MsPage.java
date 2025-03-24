package com.citc.nce.robot.api.materialSquare.vo.summary;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.robot.api.materialSquare.emums.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author bydud
 * @since 2024/6/19 11:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MsPage extends PageParam {
    @ApiModelProperty("类型")
    private List<MsType> msTypes;
    @ApiModelProperty("类型code")
    private List<Integer> msTypesCode;
    @ApiModelProperty("商品状态")
    private List<MsAuditStatus> auditStatus;
    @ApiModelProperty("模板状态")
    private List<MsTemplateStatus> templateStatus;
    @ApiModelProperty("作品库状态")
    private MsWorksLibraryStatus worksLibraryStatus;
    @ApiModelProperty("(商品)归属用户查询字段")
    private String cspQuery;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("发布者id")
    private String creator;
    @ApiModelProperty("cspId")
    private List<String> cspId;
    @ApiModelProperty("来源")
    private MsSource msSource;

    @ApiModelProperty("排序字段")
    private String sortField;

    @ApiModelProperty("排序顺序")
    private String sortOrder;

    @ApiModelProperty("isCsp")
    private Integer isCsp;

    public MsPage() {
    }

    public MsPage(PageParam pageParam) {
        super(pageParam.getPageNo(), pageParam.getPageSize(), false);
    }

    public void setAuditStatusList(MsAuditStatus auditStatus) {
        if (Objects.isNull(auditStatus)) return;
        this.auditStatus = Collections.singletonList(auditStatus);
    }

    public void setTemplateStatusList(MsTemplateStatus templateStatus) {
        if (Objects.isNull(templateStatus)) return;
        this.templateStatus = Collections.singletonList(templateStatus);
    }
}
