package com.citc.nce.robot.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 14:31
 * @Version: 1.0
 * @Description:
 */
@Data
public class ProcessStatusReq implements Serializable {
    /**
     * 流程id
     */
    @NotNull
    private Long processId;
    /**
     * 流程状态 SUCCESS 0 审核通过,FAILED  2 审核不通过(模板的状态，跟流程的状态值不一致)
     */
    @NotNull
    private  int status;
    //如果模板来自机器人流程，流程描述id
    private Long processDescId;
}
