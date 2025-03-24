package com.citc.nce.robot.controller;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.robot.RobotSceneNodeApi;
import com.citc.nce.robot.service.RobotSceneNodeService;
import com.citc.nce.robot.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 场景管理
 *
 * @Author: yangchuang
 * @Date: 2022/7/7 17:18
 * @Version: 1.0
 * @Description:
 */

@RestController()
@Slf4j
public class RobotSceneNodeController implements RobotSceneNodeApi {

    @Resource
    private RobotSceneNodeService robotSceneNodeService;

    @PostMapping("/scene/pageList")
    public PageResultResp getRobotSceneNodes(@RequestBody @Valid PageParam pageParam) {
        return robotSceneNodeService.getRobotSceneNodes(pageParam);
    }

    @PostMapping("/scene/clearChatbotWhenSceneOpen")
    public RobotSceneNodeResp clearChatbotWhenSceneOpen(RobotSceneNodeResp req) {
        return robotSceneNodeService.clearChatbotWhenSceneOpen(req);
    }

    @PostMapping("/scene/save")
    public Long saveRobotSceneNode(@RequestBody @Valid RobotSceneNodeReq robotSceneNodeReq) {
        return robotSceneNodeService.saveRobotSceneNode(robotSceneNodeReq);
    }

    @PostMapping("/scene/edit")
    public int updateRobotSceneNode(@RequestBody @Valid RobotSceneNodeEditReq robotSceneNodeEditReq) {
        return robotSceneNodeService.updateRobotSceneNode(robotSceneNodeEditReq);
    }
    public void removeChatbotAccount(String chatbotAccount) {
        robotSceneNodeService.removeChatbotAccount(chatbotAccount);
    }
    @PostMapping("/scene/delete")
    public int delRobotSceneNodeById(Long id) {
        return robotSceneNodeService.delRobotSceneNodeById(id);
    }

    @PostMapping("/scene/list")
    public List<RobotSceneNodeResp> getRobotSceneNodeList() {
        return robotSceneNodeService.getRobotSceneNodeList();
    }

    @PostMapping("/scene/listTree")
    public List<RobotSceneNodeTreeResp> getRobotSceneNodeListTree() {
        return robotSceneNodeService.getRobotSceneNodeListTree();
    }

    /**
     * 获取单个场景管理
     *
     * @param
     * @return
     */
    @PostMapping("/scene/getOne")
    @Override
    public RobotSceneNodeResp getRobotSceneNodeById(@RequestBody Long id) {
        return robotSceneNodeService.getRobotSceneNodeById(id);
    }

    @Override
    @PostMapping("/scene/queryByName")
    public RobotSceneNodeResp queryByName(@RequestBody RobotSceneNodeReq req) {
        return robotSceneNodeService.queryByName(req.getSceneName());
    }

    /**
     * 监测关联关系
     *
     * @param
     * @return
     */
    @PostMapping("/scene/checkAccountId")
    @Override
    public boolean checkAccountId(@RequestBody String id) {
        return robotSceneNodeService.checkAccountId(id);
    }

    @Override
    public Long removeBandingChatbotByChatbotAccount(String chatbotAccount) {
        return robotSceneNodeService.removeBandingChatbotByChatbotAccount(chatbotAccount);
    }
}
