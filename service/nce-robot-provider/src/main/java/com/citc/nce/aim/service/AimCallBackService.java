package com.citc.nce.aim.service;

import com.citc.nce.aim.vo.AimCallBack;
import com.citc.nce.aim.vo.AimCallBackResp;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:00
 */
public interface AimCallBackService {
    AimCallBackResp queryProjectInfo(String calling);

    AimCallBackResp smsCallBack(List<AimCallBack> req);
}
