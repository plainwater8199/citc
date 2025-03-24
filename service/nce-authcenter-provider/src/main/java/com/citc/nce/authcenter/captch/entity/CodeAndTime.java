package com.citc.nce.authcenter.captch.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/30 15:20
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class CodeAndTime implements Serializable {
    private String phone;
    private Long time;
}
