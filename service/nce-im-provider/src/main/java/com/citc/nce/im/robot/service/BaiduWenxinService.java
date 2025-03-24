package com.citc.nce.im.robot.service;

import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.session.processor.bizModel.RebotSettingModel;
import org.springframework.stereotype.Service;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/10/27 15:23
 */
@Service
public interface BaiduWenxinService {
    void exec(RebotSettingModel rebotSettingModel, MsgDto msgDto);
}
