package com.citc.nce.fileApi;

import com.citc.nce.vo.UpdateDateReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: UpFileDto
 */
@FeignClient(contextId = "FileDataApi",value = "robot-files-service" , url = "${robotFile:}")
public interface FileDataApi {

    @PostMapping(value = "/tenant/updateData")
    void updateData(@RequestBody UpdateDateReq req);
}
