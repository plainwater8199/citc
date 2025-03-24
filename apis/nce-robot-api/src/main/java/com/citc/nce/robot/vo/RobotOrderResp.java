package com.citc.nce.robot.vo;

import com.citc.nce.robot.bean.RobotOrderBean;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.vo
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  15:43
 
 * @Version: 1.0
 */
@Data
public class RobotOrderResp implements Serializable {
    private List<RobotOrderBean> list;

    //总条数
    private long total;
    //当天页数
    private long pageNo;
}
