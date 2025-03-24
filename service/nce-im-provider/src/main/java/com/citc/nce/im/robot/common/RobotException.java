package com.citc.nce.im.robot.common;

import com.citc.nce.common.core.exception.ErrorCode;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/7/24 16:04
 */
public interface RobotException {
    ErrorCode PROCESS_ERROR = new ErrorCode(820101001, "流程不存在或未发布！");
}
