package com.citc.nce.helpcenter;

import com.citc.nce.auth.helpcenter.HelpCenterEditApi;
import com.citc.nce.auth.helpcenter.HelpCenterViewApi;
import com.citc.nce.auth.helpcenter.vo.DirectoryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author yy
 * @date 2024-05-13 17:13:14
 */
@Api(tags = "帮助中心")
@RestController
public class HelpCenterController {
    @Resource
    HelpCenterViewApi helpCenterViewApi;
    @GetMapping("/help/view/getDirectory")
    @ApiOperation(value = "前端获取帮助中心内容", notes = "前端获取帮助中心内容")
    public DirectoryVo getViewDirectory() {
        return helpCenterViewApi.getViewDirectory();
    }

}
