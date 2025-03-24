package com.citc.nce.robot.api.materialSquare.vo.summary;

import com.citc.nce.common.bean.CspNameBase;
import com.citc.nce.robot.api.materialSquare.emums.MsSource;
import lombok.Data;

/**
 * @author bydud
 * @since 2024/6/24 10:47
 */
@Data
public class MsPublisherVo extends CspNameBase {
    private MsSource msSource;
}
