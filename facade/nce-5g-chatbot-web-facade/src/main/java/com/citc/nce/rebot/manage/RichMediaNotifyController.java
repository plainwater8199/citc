package com.citc.nce.rebot.manage;

import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.facadeserver.annotations.UnWrapResponse;
import com.citc.nce.robot.api.RichMediaNotifyApi;
import com.citc.nce.robot.req.RichMediaResult;
import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.vo.TemplateReq;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@UnWrapResponse
public class RichMediaNotifyController {

    @Resource
    RichMediaNotifyApi richMediaNotifyApi;

    @SkipToken
    @PostMapping({"/richMedia/messageNotify"})
    public String richMediaMessageNotify(@RequestBody RichMediaResultArray var1){
       return richMediaNotifyApi.richMediaMessageNotify(var1);
    }
    @SkipToken
    @PostMapping({"/richMedia/templateNotify"})
    public String richMediaTemplateNotify(@RequestBody RichMediaResult var1){
        return richMediaNotifyApi.richMediaTemplateNotify(var1);
    }

}
