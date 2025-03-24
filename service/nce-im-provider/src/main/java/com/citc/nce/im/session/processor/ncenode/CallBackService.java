package com.citc.nce.im.session.processor.ncenode;

import com.citc.nce.robot.vo.RobotCallBackParam;

/**
 * <p>回调函数</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2022/12/1 10:27
 */
public interface CallBackService {

    /**
     * callBackMethod
     * @param callBackParam
     * @return none
     * @author zy.qiu
     * @createdTime 2022/12/5 10:44
     */
    void callBackMethod(RobotCallBackParam callBackParam);
}
