package com.citc.nce.robotfile.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询共有运营商的文件数量
 *
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月16日09:56:05
 * @Version: 1.0
 * @Description: ExamineResult
 */
@Data
public class ExamineShareCarrierResultDo implements Serializable {
    /**
     * 文件数量
     */
    Long count;
    /**
     * 运营商
     */
    private String operator;

    /**
     * 供应商tag fontdo蜂动  owner 自有
     */
    private String supplierTag;
}
