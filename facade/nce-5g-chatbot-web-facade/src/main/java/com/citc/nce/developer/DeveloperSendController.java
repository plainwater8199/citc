package com.citc.nce.developer;

import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.developer.vo.DeveloperCustomer5gSendVo;
import com.citc.nce.developer.vo.DeveloperCustomerSendDataVo;
import com.citc.nce.developer.vo.DeveloperCustomerSendVo;
import com.citc.nce.developer.vo.DeveloperSendPhoneVo;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author ping chen
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Api(tags = "开发者服务调用")
@RequestMapping("/developer/send/")
public class DeveloperSendController {
    private final DeveloperSendApi developerSendApi;

    @PostMapping("sms")
    @ApiOperation("短信发送消息")
    @SkipToken
    @XssCleanIgnore
    public List<DeveloperSendPhoneVo> smsSend(@RequestBody @Valid DeveloperCustomerSendVo developerCustomerSendVo, @RequestParam("token") String token) {
        DeveloperCustomerSendDataVo developerCustomerSendDataVo = new DeveloperCustomerSendDataVo();
        BeanUtils.copyProperties(developerCustomerSendVo, developerCustomerSendDataVo);
        developerCustomerSendDataVo.setType(MsgTypeEnum.SHORT_MSG.getCode());
        return developerSendApi.send(developerCustomerSendDataVo, token);
    }

    @PostMapping("video")
    @ApiOperation("视频短信发送消息")
    @SkipToken
    @XssCleanIgnore
    public List<DeveloperSendPhoneVo> videoSend(@RequestBody @Valid DeveloperCustomerSendVo developerCustomerSendVo, @RequestParam("token") String token) {
        DeveloperCustomerSendDataVo developerCustomerSendDataVo = new DeveloperCustomerSendDataVo();
        BeanUtils.copyProperties(developerCustomerSendVo, developerCustomerSendDataVo);
        developerCustomerSendDataVo.setType(MsgTypeEnum.MEDIA_MSG.getCode());
        return developerSendApi.send(developerCustomerSendDataVo, token);
    }

    @SneakyThrows
    @PostMapping("5g")
    @ApiOperation("5G短信发送消息")
    @SkipToken
    @XssCleanIgnore
    public List<DeveloperSendPhoneVo> send5g(@RequestBody @Valid DeveloperCustomer5gSendVo body, @RequestParam("token") String token) {
        return developerSendApi.send5g(body,token);
    }

    @PostMapping("callBack/{id}")
    @ApiOperation("回调重试")
    public Boolean callBack(@PathVariable("id") String id) {
        return developerSendApi.callBack(id);
    }
}
