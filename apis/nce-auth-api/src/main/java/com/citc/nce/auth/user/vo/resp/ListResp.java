package com.citc.nce.auth.user.vo.resp;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/7/26 16:20
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class ListResp {
    private List<Integer> list;
}
