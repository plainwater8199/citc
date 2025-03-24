package com.citc.nce.module.schedule;


import com.citc.nce.module.service.ModuleService;
import com.citc.nce.module.vo.req.SubscribeContentSaveReq;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
@RequestMapping("/schedule/test")
public class ScheduleTestController {
    @Resource
    private ModuleService moduleService;


    @PostMapping("/query")
    public void saveSubscribeContent(SubscribeContentSaveReq req) {
        moduleService.sendSubscribeToMQ();
        System.out.println("123");
    }
}
