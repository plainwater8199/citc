package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:06
 */
@Data
@TableName("aim_project")
public class AimProjectDo extends BaseDo {

    /**
     * 项目名称
     * 同平台名称限制，唯一
     */
    private String projectName;
    /**
     * 项目编码
     * 用来识别项目，项目唯一
     */
    private String projectId;
    /**
     * 客户号码
     */
    private String calling;
    /**
     * 通道账号
     */
    private String pathKey;
    /**
     * 通道秘钥
     */
    private String secret;
    /**
     * 短信内容
     */
    private String smsTemplate;
    /**
     * 项目状态
     * 0:禁用 1:启用
     */
    private Integer projectStatus;
    /**
     * 是否删除
     * 0:未删除 1:已删除
     */
    private int deleted;

    private String creatorOld;

    private String updaterOld;

}
