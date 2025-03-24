package com.citc.nce.auth.constant;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/7/20 14:50
 * @Version 1.0
 * @Description:用户统计数量返回
 */
@Data
@Accessors(chain = true)
public class CountNum {
    private Long num;
}
