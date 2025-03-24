package com.citc.nce.robot.api;

import com.citc.nce.robot.vo.DockerInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Component
@FeignClient("nce-operate-host-service")
public interface OperateHostApi {

    @PostMapping("/operate/startDocker")
    DockerInfo startDocker();

    @PostMapping("/operate/removeDocker")
    void removeDocker(@RequestBody DockerInfo req);
}
