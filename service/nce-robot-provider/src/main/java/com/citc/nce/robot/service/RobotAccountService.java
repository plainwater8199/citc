package com.citc.nce.robot.service;

import com.citc.nce.robot.vo.RobotAccountPageReq;
import com.citc.nce.robot.vo.RobotAccountPageResultResp;
import com.citc.nce.robot.vo.RobotAccountReq;

/**
 * @Author: yangchuang
 * @Date: 2022/10/16 14:32
 * @Version: 1.0
 * @Description:
 */
public interface RobotAccountService {
    RobotAccountPageResultResp getRobotAccountList(RobotAccountPageReq robotAccountPageReq);

    int saveRobotAccount(RobotAccountReq robotAccountReq);
}
