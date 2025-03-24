package com.citc.nce.robot.vo;

import com.citc.nce.robot.bean.RobotSceneMaterialBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 14:31
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotSceneMaterialResp implements Serializable {

    List<RobotSceneMaterialBean> robotSceneMaterialBeanList;

}
