package com.citc.nce.robot.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/23 10:00
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotAccountPageResultResp implements Serializable {
    private List<RobotAccountResp> list;

    private Long total;

    private Integer pageNo;

    public RobotAccountPageResultResp(List<RobotAccountResp> list, Long total, Integer pageNo) {
        this.list = list;
        this.total = total;
        this.pageNo = pageNo;
    }

    public RobotAccountPageResultResp() {

    }
}
