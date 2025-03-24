package com.citc.nce.im.controller;

import cn.hutool.core.bean.BeanUtil;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.im.mapper.RobotGroupSendPlansDao;
import com.citc.nce.im.mapper.RobotGroupSendPlansDetailDao;
import com.citc.nce.im.service.RobotGroupSendPlanBindTaskService;
import com.citc.nce.im.service.RobotGroupSendPlansDetailService;
import com.citc.nce.robot.api.RobotGroupSendPlansDetailApi;
import com.citc.nce.robot.req.DeleteReq;
import com.citc.nce.robot.req.RobotGroupSendPlansDetailReq;
import com.citc.nce.robot.vo.RobotGroupSendPlanBindTask;
import com.citc.nce.robot.vo.RobotGroupSendPlansAndOperatorCode;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetail;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetailPageParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.controller
 * @Author:
 * @CreateTime: 2022-07-01  16:03
 * @Description:
 * @Version: 1.0
 */
@RestController
public class RobotGroupSendPlansDetailController implements RobotGroupSendPlansDetailApi {

    @Resource
    private RobotGroupSendPlansDetailService robotGroupSendPlansDetailService;

    @Resource
    private RobotGroupSendPlansDetailDao robotGroupSendPlansDetailDao;

    @Resource
    private RobotGroupSendPlansDao plansDao;

    @Resource
    private RobotGroupSendPlanBindTaskService robotGroupSendPlanBindTaskService;


    /**
     * 根据planId查询到所有的盒子
     *
     * @param robotGroupSendPlansDetailReq
     * @return
     */
    @Override
    @PostMapping("/group/detail/queryById")
    public RobotGroupSendPlansDetail queryById(@RequestBody RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq) {
        return robotGroupSendPlansDetailService.queryById(robotGroupSendPlansDetailReq.getId());
    }

    @Override
    @PostMapping("/group/detail/queryByPage")
    @XssCleanIgnore
    public PageResult queryByPage(@RequestBody RobotGroupSendPlansDetailPageParam robotGroupSendPlansDetailPageParam) {
        return robotGroupSendPlansDetailService.queryByPage(robotGroupSendPlansDetailPageParam);
    }

    @Override
    @PostMapping("/group/detail/insert")
    public Long insert(@RequestBody RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq) {
        return robotGroupSendPlansDetailService.insert(robotGroupSendPlansDetailReq);
    }

    private String getMessageType(Integer type) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "文本");
        map.put(2, "图片");
        map.put(3, "视频");
        map.put(4, "音频");
        map.put(5, "文件");
        map.put(6, "单卡");
        map.put(7, "多卡");
        map.put(8, "位置");
        return map.get(type);
    }

    @Override
    @PostMapping("/group/detail/update")
    public void update(@RequestBody @Valid RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq) {
        robotGroupSendPlansDetailService.updateDate(robotGroupSendPlansDetailReq);
    }

    @Override
    @PostMapping("/group/detail/deleteById")
    public void deleteById(@RequestBody RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq) {
        robotGroupSendPlansDetailService.deleteById(robotGroupSendPlansDetailReq.getId());
    }

    @PostMapping("/group/detail/updateAmount")
    @Override
    public void updateAmount(@RequestBody RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq) {
        robotGroupSendPlansDetailService.updateAmount(robotGroupSendPlansDetailReq);
    }

    @Override
    @PostMapping("/group/detail/batchDelete")
    public void batchDelete(DeleteReq deleteReq) {
        robotGroupSendPlansDetailDao.logicDeleteByIds(deleteReq.getIds());
    }

    @Override
    @PostMapping("/group/detail/selectAllByPlanId")
    public List<RobotGroupSendPlansDetail> selectAllByPlanId(@RequestBody RobotGroupSendPlansDetailReq req) {
        plansDao.checkPlanIsOwn(req.getPlanId());
        return robotGroupSendPlansDetailService.selectAllByPlanId(req);
    }

    @Override
    public RobotGroupSendPlansAndOperatorCode selectByTaskId(String taskId) {

        RobotGroupSendPlansAndOperatorCode result = new RobotGroupSendPlansAndOperatorCode();
        RobotGroupSendPlanBindTask robotGroupSendPlanBindTask = robotGroupSendPlanBindTaskService.queryByTaskId(taskId);
        if (robotGroupSendPlanBindTask != null) {
            Long planDetailId = robotGroupSendPlanBindTask.getPlanDetailId();
            RobotGroupSendPlansDetail robotGroupSendPlansDetail = robotGroupSendPlansDetailService.queryById(planDetailId);
            result.setId(robotGroupSendPlansDetail.getId());
            result.setPlanId(robotGroupSendPlansDetail.getPlanId());
            result.setCustomerId(robotGroupSendPlanBindTask.getCustomerId());
            result.setOperatorCode(robotGroupSendPlanBindTask.getOperatorCode());
            result.setAppId(robotGroupSendPlanBindTask.getAppId());
            result.setOldMessageId(robotGroupSendPlanBindTask.getOldMessageId());
            return result;
        }
        throw new BizException(100045232, "5G阅信蜂动群发任务不存在");
    }

    @Override
    public List<RobotGroupSendPlansAndOperatorCode> getByTaskIds(List<String> taskIds) {
        List<RobotGroupSendPlanBindTask> robotGroupSendPlanBindTask = robotGroupSendPlanBindTaskService.queryByTaskIds(taskIds);
        if (robotGroupSendPlanBindTask != null) {
            return BeanUtil.copyToList(robotGroupSendPlanBindTask, RobotGroupSendPlansAndOperatorCode.class);
        }
        throw new BizException(100045232, "5G阅信蜂动群发任务不存在");

    }

    @Override
    @PostMapping("/group/detail/selectAllSchedule")
    public List<Long> selectAllSchedule() {
        return robotGroupSendPlansDetailService.selectAllSchedule();

    }

    @Override
    @PostMapping("/group/detail/updateExpiredData")
    public List<Long> updateExpiredData(@RequestBody List<Long> ids) {
        return robotGroupSendPlansDetailService.updateExpiredData(ids);
    }
}
