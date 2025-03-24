package com.citc.nce.customcommand;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.facadeserver.verifyCaptcha.VerifyCaptchaV2;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.utils.dh.ECDHService;
import com.citc.nce.customcommand.vo.CustomCommandRequirementAddReq;
import com.citc.nce.customcommand.vo.CustomCommandRequirementDetailVo;
import com.citc.nce.customcommand.vo.CustomCommandRequirementSimpleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author jiancheng
 */
@RestController
@Api(value = "customCommandController", tags = "自定义指令需求管理")
@RequiredArgsConstructor
@RequestMapping("customCommand/requirement")
public class CustomCommandRequirementController {
    private final CustomCommandRequirementApi customCommandRequirementApi;
    private final ECDHService ecdhService;

    @PostMapping("add")
    @ApiOperation("新增需求")
    @VerifyCaptchaV2
    public void add(@RequestBody @Valid CustomCommandRequirementAddReq addReq) {
        customCommandRequirementApi.add(addReq);
    }

    @GetMapping("{requirementId}")
    @ApiOperation("查询需求详情")
    public CustomCommandRequirementDetailVo getRequirementDetail(@PathVariable Long requirementId) {
        CustomCommandRequirementDetailVo requirementDetail = customCommandRequirementApi.getRequirementDetail(requirementId);
        if (!SessionContextUtil.getUser().getUserId().equals(requirementDetail.getCustomerId())) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        return requirementDetail;
    }

    @PostMapping("mine")
    @ApiOperation("查询我的需求列表")
    public PageResult<CustomCommandRequirementSimpleVo> getMyRequirements(@RequestBody @Valid PageParam pageParam) {
        PageResult<CustomCommandRequirementSimpleVo> page = customCommandRequirementApi.getMyRequirements(pageParam);
        if (!CollectionUtils.isEmpty(page.getList())) {
            for (CustomCommandRequirementSimpleVo vo : page.getList()) {
                vo.setContactPhone(ecdhService.encode(vo.getContactPhone()));
            }
        }
        return page;
    }

}
