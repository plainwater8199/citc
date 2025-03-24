package com.citc.nce.aim.privatenumber.service;

import com.citc.nce.aim.privatenumber.vo.PrivateNumberCallBack;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberCallBackResp;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:00
 */
public interface PrivateNumberBackService {

    PrivateNumberCallBackResp queryProjectInfo(String appKey);

    PrivateNumberCallBackResp smsCallBack(List<PrivateNumberCallBack> req);
}
