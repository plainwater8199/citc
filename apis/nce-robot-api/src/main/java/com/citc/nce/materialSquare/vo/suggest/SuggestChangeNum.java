package com.citc.nce.materialSquare.vo.suggest;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 2024/5/15 16:35
 */
@Data
@Valid
public class SuggestChangeNum {
    @NotNull(message = "推荐表id")
    private Long msSuggestId;
    @NotNull(message = "排序不能为空")
    private Long orderNum;
}
