package com.citc.nce.authcenter.utils;

import com.citc.nce.authcenter.Utils.UtilsApi;
import com.citc.nce.authcenter.utils.service.NacosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController()
@Slf4j
public class UtilsController implements UtilsApi {

    @Resource
    private NacosService nacosService;

    @Override
    @PostMapping("/checkIPVisitCount")
    public void checkIPVisitCount(@RequestParam("ip") String ip) {
        nacosService.queryNacosServices();
    }

}
