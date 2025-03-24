package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
//@TableName("mass_segment")
public class MassSegmentDo extends BaseDo<MassSegmentDo> implements Serializable {

    /**
     * 号段
     */
    private String phoneSegment;

    /**
     * 运营商
     */
    private String operator;

    /**
     * 是否删除
     */
    private Integer deleted;

    /**
     * 删除时间
     */
    private Date deleteTime;

    private String creatorOld;

    private String updaterOld;

}
