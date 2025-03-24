package com.citc.nce.robot.service;

import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.robot.vo.*;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.service
 * @Author: weilanglang
 * @CreateTime: 2022-07-06  17:38
 
 * @Version: 1.0
 */
public interface RobotProcessTreeService {

    /**
     *  保存流程设计图
     * */
    public void saveProcessDes(RobotProcessTreeReq processTreeReq);

    /**
     * 模板审核后，流程状态回调
     * @param processStatusReq
     */
    void processStatusCallback(ProcessStatusReq processStatusReq);

    /**
     *  根据流程id，查询设计图
     * */
    public RobotProcessTreeResp listProcessDesById(Long processId);

    /**
     *  根据流程id，查询设计图（发布状态）
     * */
    public RobotProcessTreeResp listReleaseProcessDesById(Long processId);
    /**
     * 根据流程id，发布设计图
     * */
    public RobotProcessTemplateAuditResp releaseProcessDesById(long processId, String userId);

    /**
     * 蜂动上线流程初始化，临时接口
     */
    public void oldProcessToAuditInit();
     PrepareForReleaseProcessResp prepareForReleaseProcess(RobotProcessTreeReq processTreeReq, long processId, BaseUser user) ;

    /**
     *  查询所有提问节点
     * */
    public RobotProcessAskNodeResp listAllAskNode();

    /**
     * 检查发布了的流程中占用的素材
     * @return json集合
     */
    List<String> checkUsed();
    /**
     *  检查所有未删除流程占用的素材
     * */
    public boolean checkUsedByAll(String fileUuid);
    RobotShortcutButtonResp getRobotShortcutButtonResp(String uuid);

    /**
     * 删除流程详情
     * @param map
     * @return
     */
      int  deleteProcessDesc(HashMap<String,Object> map);
}
