package com.citc.nce.robot.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zhujy
 * @version 1.0
 * @date 2022/8/1 11:20
 * @description 给开发服务此对象,用以绑定它的群发计划和supplier群发任务
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FontdoGroupSendResultReq implements Serializable {
    /*
     * supplier群发任务ID
     * */
    String taskId;
    /*
     * 网关自己生成的消息ID,用来和planDetailId群发任务绑定
     * */
    String msgId;
    /*
     * 0  成功.  其他数字失败
     * */
    private String code;
    //描述
    private String message;
}