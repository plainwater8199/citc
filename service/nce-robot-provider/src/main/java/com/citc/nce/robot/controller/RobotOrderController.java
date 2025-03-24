package com.citc.nce.robot.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.robot.RobotOrderApi;
import com.citc.nce.robot.bean.OrderPageParam;
import com.citc.nce.robot.bean.RobotOrderBean;
import com.citc.nce.robot.exception.RobotErrorCode;
import com.citc.nce.robot.service.RobotOrderService;
import com.citc.nce.robot.vo.RobotOrderReq;
import com.citc.nce.robot.vo.RobotOrderResp;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.controller
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  16:03
 * @Description: 机器人设置--指令管理控制层
 * @Version: 1.0
 */
@RestController
@RequestMapping("/robot")
public class RobotOrderController implements RobotOrderApi {

    @Resource
    RobotOrderService robotOrderService;

    @PostMapping("/order/save")
    public void saveOrder(RobotOrderReq robotOrderReq) {

        /**
         * 2023-11-13
         * 自定义指令不走原有体系
         */
        if (null != robotOrderReq.getOrderType() && 1 == robotOrderReq.getOrderType()) {
            return;
        }

        String orderName = robotOrderReq.getOrderName();
        String requestUrl = robotOrderReq.getRequestUrl();

        if (StrUtil.isBlank(orderName)) {
            throw new BizException(RobotErrorCode.ORDER_NAME_ORDER_BAD_REQUEST);
        }
       /* if (StrUtil.isBlank(requestUrl)){
            throw new BizException(RobotErrorCode.ORDER_URL_DATA_EXIST);
        }*/

        BaseUser user = SessionContextUtil.getUser();
//        if (ObjectUtil.isNull(user)){
//            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
//        }

        // 保存前，先验证指令名称是否存在
        RobotOrderResp orderResp = robotOrderService.listOrderByName(orderName);
        if (ObjectUtil.isNotNull(orderResp) && !orderResp.getList().isEmpty()) {
            //改变量名称已经存在
            throw new BizException(RobotErrorCode.ORDER_DATA_EXIST);
        }
        robotOrderService.saveOrder(robotOrderReq, user);
    }


    @Override
    @PostMapping("/order/queryByTsOrderId")
    public List<RobotOrderBean> queryByTsOrderId(RobotOrderReq req) {
        return robotOrderService.queryByTsOrderId(req.getTsOrderId());
    }

    @PostMapping("/order/edit")
    public void compileOrder(RobotOrderReq robotOrderReq) {
        /**
         * 2023-11-13
         * 自定义指令不走原有体系
         */
        if (1 == robotOrderReq.getOrderType()) {
            return;
        }

        Long id = robotOrderReq.getId();
        String orderName = robotOrderReq.getOrderName();
        if (StrUtil.isBlank(orderName)) {
            throw new BizException(RobotErrorCode.ORDER_NAME_ORDER_BAD_REQUEST);
        }
        if (ObjectUtil.isNull(id) || id == 0) {
            throw new BizException(RobotErrorCode.ORDER_ID_DATA_EXIST);
        }

        // 编辑保存前，先验证指令名称是否存   在或者是否是自身
        RobotOrderResp orderResp = robotOrderService.listOrderByName(orderName);
        if (ObjectUtil.isNotNull(orderResp) && !CollectionUtils.isEmpty(orderResp.getList())) {
            List<RobotOrderBean> orderList = orderResp.getList();
            for (RobotOrderBean orderBean : orderList) {
                if (!orderBean.getId().equals(robotOrderReq.getId())) {
                    //改变量名称已经存在并且不是自身
                    throw new BizException(RobotErrorCode.ORDER_DATA_EXIST);
                }
            }
        }
        robotOrderService.editOrder(robotOrderReq);
    }

    @PostMapping("/order/list")
    public RobotOrderResp listAllOrder(OrderPageParam pageParam) {
        BaseUser user = SessionContextUtil.getUser();
//        if (ObjectUtil.isNull(user)){
//            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
//        }
        if (pageParam.getUserId() != null && user != null)
            pageParam.setUserId(user.getUserId());
        return robotOrderService.listAll(pageParam);
    }


    @PostMapping("/order/queryOneById")
    public RobotOrderBean queryOneById(OrderPageParam pageParam) {
        return robotOrderService.queryOneById(pageParam.getId());
    }

    @PostMapping("/order/delete")
    public void removeOrder(RobotOrderReq robotOrderReq) {
        Long id = robotOrderReq.getId();
        if (ObjectUtil.isNull(id) || id == 0) {
            throw new BizException(RobotErrorCode.ORDER_ID_DATA_EXIST);
        }
        BaseUser user = SessionContextUtil.getUser();
        robotOrderService.removeOrder(robotOrderReq, user);
    }
}
