package com.citc.nce.im.mall.snapshot;

import com.citc.nce.im.mall.snapshot.service.MallSnapshotService;
import com.citc.nce.im.mall.variable.service.MallRobotVariableService;
import com.citc.nce.robot.api.mall.common.*;
import com.citc.nce.robot.api.mall.snapshot.MallSnapshotApi;
import com.citc.nce.robot.api.mall.snapshot.req.MallRobotOrderSaveOrUpdateReq;
import com.citc.nce.robot.api.mall.snapshot.resp.MallRobotOrderSaveOrUpdateResp;
import com.citc.nce.robot.api.mall.variable.vo.resp.MallRobotVariableDetailResp;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>扩展商城-商品</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/21 16:59
 */
@RestController
public class MallSnapshotController implements MallSnapshotApi {

    @Resource
    MallSnapshotService service;
    @Resource
    MallRobotVariableService variableService;

    @Override
    public MallRobotOrderSaveOrUpdateResp saveOrUpdate(MallRobotOrderSaveOrUpdateReq req) {
        MallCommonContent view = req.getMallCommonContent();
        if (Objects.nonNull(view)) {
            if (!CollectionUtils.isEmpty(view.getRobot())) {
                for (Robot robot : view.getRobot()) {
                    List<RobotInputUtil.Variable> variables = new LinkedList<>();
                    //把前段传递变量加上，如果没设置类型就默认为自定义变量
                    if (!CollectionUtils.isEmpty(robot.getVariables())) {
                        List<RobotInputUtil.Variable> reqVarList = robot.getVariables().stream().map(
                                s -> new RobotInputUtil.Variable(s.getId(), s.getVariableName(), StringUtils.hasLength(s.getType()) ? s.getType() : RobotInputUtil.type_custom)
                        ).collect(Collectors.toList());
                        variables.addAll(reqVarList);
                    }
                    //获取流程图中的变量
                    RobotProcess process = robot.getProcess();
                    if (Objects.nonNull(process)) {
                        List<RobotInputUtil.Variable> p = RobotInputUtil.getVariables(process.getProcessDes());
                        if (!CollectionUtils.isEmpty(p)) variables.addAll(p);
                        List<RobotInputUtil.Variable> b = RobotInputUtil.getVariables(process.getRobotShortcutButtons());
                        if (!CollectionUtils.isEmpty(p)) variables.addAll(b);
                    }
                    //获取指令中的变量
                    List<RobotOrder> orders = robot.getOrders();
                    if (!CollectionUtils.isEmpty(orders)) {
                        for (RobotOrder order : orders) {
                            List<RobotInputUtil.Variable> url = RobotInputUtil.getVariables(order.getRequestUrlName());
                            if (!CollectionUtils.isEmpty(url)) variables.addAll(url);

                            List<RobotInputUtil.Variable> headerList = RobotInputUtil.getVariables(order.getHeaderList());
                            if (!CollectionUtils.isEmpty(headerList)) variables.addAll(headerList);

                            List<RobotInputUtil.Variable> bodyList = RobotInputUtil.getVariables(order.getBodyList());
                            if (!CollectionUtils.isEmpty(bodyList)) variables.addAll(bodyList);

                            List<RobotInputUtil.Variable> responseList = RobotInputUtil.getOrderRespVariables(order.getResponseList());
                            if (!CollectionUtils.isEmpty(responseList)) variables.addAll(responseList);
                        }
                    }
                    //资源系统变量和自定义变量，去除
                    variables = variables.stream().filter(s -> RobotInputUtil.type_sys.equalsIgnoreCase(s.getType()) || RobotInputUtil.type_custom.equalsIgnoreCase(s.getType()))
                            .distinct().collect(Collectors.toList());

                    Map<String, RobotVariable> variableMap = variables.stream().map(s -> {
                        RobotVariable robotVariable = new RobotVariable();
                        robotVariable.setId(s.getId());
                        robotVariable.setVariableName(s.getVariableName());
                        robotVariable.setType(s.getType());
                        return robotVariable;
                    }).collect(Collectors.toMap(RobotVariable::getId, Function.identity()));

                    //查询已存在的变量
                    List<MallRobotVariableDetailResp> variableDetail = variableService.listByIds(variables.stream()
                            .filter(this::canToMum)
                            .map(RobotInputUtil.Variable::getId)
                            .collect(Collectors.toList()));
                    //修改默认值为自己变量的
                    if (!CollectionUtils.isEmpty(variableDetail)) {
                        for (MallRobotVariableDetailResp resp : variableDetail) {
                            RobotVariable variable = variableMap.get(resp.getId());
                            if (Objects.nonNull(variable)) {
                                variable.setVariableValue(resp.getVariableValue());
                                variable.setCreateTime(resp.getCreateTime());
                            }
                        }
                    }
                    robot.setVariables(new ArrayList<>(variableMap.values()));
                }
            }
        }
        return service.saveOrUpdateSnapshot(req);
    }

    //变量id是long类型的
    private boolean canToMum(RobotInputUtil.Variable variable) {
        String id = variable.getId();
        if (StringUtils.hasLength(id)) {
            try {
                Long l = Long.valueOf(id);
                return true;
            } catch (NumberFormatException exception) {
                return false;
            }
        }
        return false;
    }
}
