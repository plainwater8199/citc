package com.citc.nce.robot.service;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.robot.vo.*;

import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/7 17:20
 * @Version: 1.0
 * @Description:
 */
public interface RobotSceneNodeService {

    PageResultResp getRobotSceneNodes(PageParam pageParam);

    Long saveRobotSceneNode(RobotSceneNodeReq robotSceneNodeReq);

    int updateRobotSceneNode(RobotSceneNodeEditReq robotSceneNodeEditReq);
    void removeChatbotAccount(String chatbotAccount) ;

        int delRobotSceneNodeById(Long id);

    List<RobotSceneNodeResp> getRobotSceneNodeList();

    List<RobotSceneNodeTreeResp> getRobotSceneNodeListTree();

    RobotSceneNodeResp getRobotSceneNodeById(Long id);

    boolean checkAccountId(String id);

    RobotSceneNodeResp clearChatbotWhenSceneOpen(RobotSceneNodeResp req);

    RobotSceneNodeResp queryByName(String sceneName);

    Long removeBandingChatbotByChatbotAccount(String chatbotAccount);
}
