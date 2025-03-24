package com.citc.nce.robot.vo.tempStore;

/*
 *
 * @author bydud
 * @since 2024/2/21
 */

import com.citc.nce.robot.vo.RobotOrderReq;
import lombok.Data;

import java.util.List;

@Data
public class Csp4CustomerOrder {
    /**
     * 有重复
     */
    List<Long> duplicateList;
    List<RobotOrderReq> saveResult;

}
