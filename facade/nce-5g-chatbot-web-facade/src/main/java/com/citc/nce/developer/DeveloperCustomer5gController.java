package com.citc.nce.developer;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.web.utils.dh.ECDHService;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.developer.vo.DeveloperCustomerInfoVo;
import com.citc.nce.developer.vo.DeveloperCustomerVo;
import com.citc.nce.developer.vo.DeveloperSaveCallbackUrlVo;
import com.citc.nce.developer.vo.SmsDeveloperSendSearchVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author ping chen
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Api(tags = "开发者服务5g")
@RequestMapping("/developer/customer/5g/")
public class DeveloperCustomer5gController {
    private final DeveloperCustomer5gApi developerCustomer5gApi;
    private final ECDHService ecdhService;

    @PostMapping("queryInfo")
    @ApiOperation("获取开发者信息详情")
    public DeveloperCustomerInfoVo queryInfo() {
        return developerCustomer5gApi.queryInfo();
    }

    @PostMapping("saveCallbackUrl")
    @ApiOperation("设置回调地址")
    public void saveCallbackUrl(@RequestBody @Valid DeveloperSaveCallbackUrlVo developerSaveCallbackUrlVo) {
        developerCustomer5gApi.saveCallbackUrl(developerSaveCallbackUrlVo);
    }

    @PostMapping("search/callbacks")
    @ApiOperation("回调明细列表查询")
    @XssCleanIgnore
    public PageResult<DeveloperCustomerVo> searchDeveloperSend(@RequestBody @Valid SmsDeveloperSendSearchVo smsDeveloperSendSearchVo) {
        PageResult<DeveloperCustomerVo> page = developerCustomer5gApi.searchDeveloperSend(smsDeveloperSendSearchVo);
        for (DeveloperCustomerVo body : page.getList()) {
            body.setPhone(ecdhService.encode(body.getPhone()));
        }
        return page;
    }
}
