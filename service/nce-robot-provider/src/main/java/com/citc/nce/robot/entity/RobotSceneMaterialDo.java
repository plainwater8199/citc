package com.citc.nce.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 15:11
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("robot_scene_material")
@Accessors(chain = true)
public class RobotSceneMaterialDo extends BaseDo<RobotSceneMaterialDo>  {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 流程id
     */
    private Long processId;
    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 素材id
     */
    private Long materialId;

    /**
     * 1图片2视频3音频4文件
     */
    private int materialType;

    /**
     * 0未删除  1已删除
     */
    private int deleted;

    /**
     * 删除时间
     */
    private Date deleteTime;

    public RobotSceneMaterialDo() {
    }

}
