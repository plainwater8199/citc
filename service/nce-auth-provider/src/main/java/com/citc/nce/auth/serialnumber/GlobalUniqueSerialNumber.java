package com.citc.nce.auth.serialnumber;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jcrenc
 * @since 2024/5/23 14:49
 */
@TableName("global_unique_serial_number")
@Data
@Accessors(chain = true)
public class GlobalUniqueSerialNumber {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Integer type;

    private String serialNumber;
}
