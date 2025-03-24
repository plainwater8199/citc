package com.citc.nce.developer.controller;

import com.citc.nce.developer.service.DeveloperSendService;
import com.citc.nce.developer.vo.DeveloperSendPhoneVo;
import com.citc.nce.robot.req.RichMediaResultArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class DeveloperTestController {

    @Resource
    private DeveloperSendService developerSendService;
    @PostMapping("video/developerTest")
    public void developerTest(@RequestBody  RichMediaResultArray richMediaResultArray){
        DeveloperSendPhoneVo developerSendPhoneVo = new DeveloperSendPhoneVo();
        developerSendPhoneVo.setDeveloperSenId("f25d8d743b9e46db8ab23afa99548d9e");
        developerSendService.updateVideoDeveloperSendPlatformResult(developerSendPhoneVo, richMediaResultArray);
    }


}
