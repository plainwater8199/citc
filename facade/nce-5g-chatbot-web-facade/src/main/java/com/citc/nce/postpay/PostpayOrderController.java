package com.citc.nce.postpay;

import com.citc.nce.auth.postpay.PostpayOrderApi;
import com.citc.nce.auth.postpay.order.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.web.utils.dh.ECDHService;
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
import java.util.List;

/**
 * @author jcrenc
 * @since 2024/3/12 14:09
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/postpay/order")
@Validated
@Api(tags = "后付费订单")
public class PostpayOrderController {
    private final PostpayOrderApi postpayOrderApi;
    private final ECDHService ecdhService;

    @PostMapping("search")
    @ApiOperation("csp搜索后付费订单")
    public PageResult<PostpayOrderVo> searchPostpayOrder(@RequestBody @Valid PostpayOrderQueryVo req) {
        PageResult<PostpayOrderVo> result = postpayOrderApi.searchPostpayOrder(req);
        result.getList().forEach(s->s.setPhone(ecdhService.encode(s.getPhone())));
        return result;
    }

    @PostMapping("/customer/search")
    @ApiOperation("客户搜索后付费订单")
    public PageResult<PostpayOrderCustomerVo> customerSearchPostpayOrder(@RequestBody PostpayOrderQueryVo req) {
        return postpayOrderApi.customerSearchPostpayOrder(req);
    }

    @PostMapping("details")
    @ApiOperation("查询订单明细")
    public List<PostpayOrderDetailVo> selectOrderDetail(@RequestBody @Valid PostpayOrderDetailReq req) {
        return postpayOrderApi.selectOrderDetail(req);
    }

    @PostMapping("note")
    @ApiOperation("备注订单")
    public void noteOrder(@RequestBody @Valid PostpayOrderNoteVo noteVo) {
        postpayOrderApi.noteOrder(noteVo);
    }

    @PostMapping("finishPay")
    @ApiOperation("完成支付")
    public void finishPay(@RequestBody @Valid PostpayOrderFinishVo finishVo) {
        postpayOrderApi.finishPay(finishVo);
    }

}
