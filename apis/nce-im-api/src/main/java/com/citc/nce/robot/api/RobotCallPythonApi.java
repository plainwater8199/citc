package com.citc.nce.robot.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * <p>自定义指令调用python服务api</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2022/12/7 16:58
 */
@Service
@Component
@FeignClient(value = "python-server")
public interface RobotCallPythonApi {

    /**
     * callPython
     * @param param
     * @return none
     * @author zy.qiu
     * @createdTime 2022/12/7 17:01
     */
    @PostMapping("/user/defined/directive")
    void callPython(String param);

}
