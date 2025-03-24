package com.citc.nce.misc.utils;

import com.citc.nce.misc.constant.SystemTimeApi;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/24 20:57
 * @Version 1.0
 * @Description:
 */
@RestController
public class SystemTimeController  {
    /**
     * 获取时间戳
     */

    @PostMapping("/getSystemTime")
    public Long getSystemTime(@RequestBody ReqTest req) {

        System.out.println("12313");
        return SystemClock.millisClock().now();
    }
}
