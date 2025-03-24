package com.citc.nce.robot.api.materialSquare.vo.summary;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.robot.api.materialSquare.emums.MsEsOrder;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author bydud
 * @since 2024/7/1 15:09
 */
@Data
public class MsEsPageQuery extends PageParam {
    private String queryStr;
    private MsType msType;
    private String msTag;
    @NotNull
    private MsEsOrder orderType;

    @ApiModelProperty(value = "是否需要推荐元素(默认为false)")
    private boolean needSuggestions = false;

    private List<MsEsPageResult> suggestions;
}
