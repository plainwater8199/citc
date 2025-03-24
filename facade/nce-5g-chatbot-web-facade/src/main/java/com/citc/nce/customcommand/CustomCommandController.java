package com.citc.nce.customcommand;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.customcommand.vo.MyAvailableCustomCommandReq;
import com.citc.nce.customcommand.vo.MyAvailableCustomCommandVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author jiancheng
 */
@RestController
@Api(value = "customCommandController", tags = "自定义指令生产")
@RequiredArgsConstructor
@RequestMapping("customCommand")
public class CustomCommandController {
    private final CustomCommandApi customCommandApi;

    @PostMapping("my/available")
    @ApiOperation("查询用户可用指令")
    public PageResult<MyAvailableCustomCommandVo> getMyAvailableCommand(@RequestBody @Valid MyAvailableCustomCommandReq req) {
        return customCommandApi.getMyAvailableCommand(req);
    }

    @PostMapping("my")
    @ApiOperation("查询登录用户指令")
    public PageResult<MyAvailableCustomCommandVo> getMyCommand(@RequestBody @Valid MyAvailableCustomCommandReq req) {
        return customCommandApi.getMyCommand(req);
    }

}
