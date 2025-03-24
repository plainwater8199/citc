package com.citc.nce.robot.api.materialSquare.vo.summary;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 素材广场发布草稿
 *
 * @author bydud
 * @since 2024/5/31 10:18
 */

@Data
@Accessors(chain = true)
public class MsSummaryUpdateDart extends  MsSummarySaveDart implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long mssId;
}
