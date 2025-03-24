package com.citc.nce.auth.csp.dict.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 15:10
 */
@Data
@TableName("csp_dict")
public class CspDictDo extends BaseDo<CspDictDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dictCode;

    private String DictValue;

    private Integer dictType;

    private int deleted;
}
