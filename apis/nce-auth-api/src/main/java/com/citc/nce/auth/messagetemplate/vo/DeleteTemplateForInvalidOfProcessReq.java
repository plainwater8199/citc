package com.citc.nce.auth.messagetemplate.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 删除机器人流程无效的模板
 * @author yy
 * @date 2024-05-29 17:16:51
 */
@Data
public class DeleteTemplateForInvalidOfProcessReq {
    /**
     * 如果模板来自机器人流程，流程描述id
     */
    @NotEmpty(message = "流程id不能为空")
    Long processId;
    /**
     * 机器人流程中包含的模板id，删除不包含在templateIds的模板
     */
    @NotEmpty(message = "模板id不能为空")
    List<Long> templateIds;
}
