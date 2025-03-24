package com.citc.nce.robot.vo.tempStore;

/*
 *
 * @author bydud
 * @since 2024/2/21
 */

import com.citc.nce.robot.vo.RobotVariableReq;
import lombok.Data;

import java.util.List;

@Data
public class Csp4CustomerVariable {
    /**
     * 有重复
     */
    List<Long> duplicateList;
    List<RobotVariableReq> saveResult;

}
