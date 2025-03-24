package com.citc.nce.robot.vo;


import com.citc.nce.robot.bean.RobotVariableBean;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: weilanglang
 * @Contact: llweix
 * @Date: 2022/6/30 17:09
 * @Version: 1.0
 * @Description:  机器人设置--变量管理
 */
@Data
public class RobotVariableResp implements Serializable {

    List<RobotVariableBean> list;
    //总条数
    private long total;
    //当天页数
    private long pageNo;

}
