package com.citc.nce.robot;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.robot.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/7 15:16
 * @Version: 1.0
 * @Description:
 */
@FeignClient(value = "rebot-service", contextId = "RobotSceneNode",url = "${robot:}")
public interface  RobotSceneNodeApi {
    /**
     * 场景管理列表获取
     *
     * @param
     * @return
     */
    @PostMapping("/scene/pageList")
    PageResultResp getRobotSceneNodes(@RequestBody  @Valid PageParam pageParam);

    @PostMapping("/scene/clearChatbotWhenSceneOpen")
    RobotSceneNodeResp clearChatbotWhenSceneOpen(@RequestBody  @Valid RobotSceneNodeResp req);

    /**
     * 新增场景管理
     *
     * @param
     * @return
     */
    @PostMapping("/scene/save")
    Long saveRobotSceneNode(@RequestBody  @Valid RobotSceneNodeReq robotSceneNodeReq);

    /**
     * 修改场景管理
     *
     * @param
     * @return
     */
    @PostMapping("/scene/edit")
    int updateRobotSceneNode(@RequestBody  @Valid RobotSceneNodeEditReq robotSceneNodeEditReq);
    @GetMapping("/scene/removeChatbot")
    void removeChatbotAccount(@RequestParam("chatbotAccount") String chatbotAccount) ;
    /**
     * 删除场景管理
     *
     * @param
     * @return
     */
    @PostMapping("/scene/delete")
    int delRobotSceneNodeById(@RequestBody Long id);

    @PostMapping("/scene/list")
    List<RobotSceneNodeResp> getRobotSceneNodeList();

    @PostMapping("/scene/listTree")
    List<RobotSceneNodeTreeResp> getRobotSceneNodeListTree();

    /**
     * 获取单个场景管理
     *
     * @param
     * @return
     */
    @PostMapping("/scene/getOne")
    RobotSceneNodeResp getRobotSceneNodeById(@RequestBody Long id);

    @PostMapping("/scene/queryByName")
    RobotSceneNodeResp queryByName(@RequestBody RobotSceneNodeReq req);

    /**
     * 监测关联关系
     *
     * @param
     * @return
     */
    @PostMapping("/scene/checkAccountId")
    boolean checkAccountId(@RequestBody String id);

    @PostMapping("/scene/removeBandingChatbotByChatbotAccount")
    Long removeBandingChatbotByChatbotAccount(@RequestParam("chatbotAccount") String chatbotAccount);
}
