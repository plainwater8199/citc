package com.citc.nce.robotfile.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.dto.FileReq;
import com.citc.nce.dto.FileTidReq;
import com.citc.nce.robot.RobotProcessTreeApi;
import com.citc.nce.robot.api.RobotGroupSendPlansApi;
import com.citc.nce.robotfile.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.robotfile.entity.AudioDo;
import com.citc.nce.robotfile.entity.PictureDo;
import com.citc.nce.robotfile.entity.UpFileDo;
import com.citc.nce.robotfile.entity.VideoDo;
import com.citc.nce.robotfile.enums.MaterialEnum;
import com.citc.nce.robotfile.mapper.*;
import com.citc.nce.robotfile.service.IAudioService;
import com.citc.nce.robotfile.service.IPictureService;
import com.citc.nce.robotfile.service.IUpFileService;
import com.citc.nce.robotfile.service.IVideoService;
import com.citc.nce.vo.StatisticsResp;
import com.citc.nce.vo.StatusTotal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 对接其它服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
public class PlatformService {

    @Resource
    AudioMapper audioMapper;

    @Resource
    VideoMapper videoMapper;

    @Resource
    PictureMapper pictureMapper;

    @Resource
    UpFileMapper upFileMapper;

    @Resource
    ExamineResultMapper examineResultMapper;

    @Resource
    RobotGroupSendPlansApi sendPlansApi;

    @Resource
    MessageTemplateApi messageTemplateApi;

    @Resource
    RobotProcessTreeApi robotProcessTreeApi;

    @Resource
    AccountManagementApi accountManagementApi;


    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;
    public Boolean fileUsedAllPossible(FileReq fileReq) {
        String fileUuid = fileReq.getFileUuid();
        List<String> jsons = messageTemplateApi.selectAllTemplateJsonByCreator();
        boolean contains=jsons.stream().anyMatch(templateJson->templateJson.contains(fileUuid));
        if(contains)return true;
        return robotProcessTreeApi.checkUsedByAll(fileUuid);
    }
    public Boolean fileUsed(FileReq fileReq) {
        String fileUuid = fileReq.getFileUuid();
        //查询复合状态的所有模板id
        List<Long> templateIds = sendPlansApi.selectAllPlanIds();
        //通过模板id查找到所有的json
        List<String> jsons = messageTemplateApi.selectAllTemplateJson(templateIds);
        //遍历json集合获取到里面所有的素材uuid集合
        for (String json : jsons) {
            //看集合里面是否有对应的Uuid
            //有就返回true,没有就false
            JSONObject jsonObject = JSONObject.parseObject(json);
            if (StringUtils.isNotEmpty(jsonObject.getString("audioUrlId"))){
                if (StringUtils.equals(fileUuid,jsonObject.getString("audioUrlId"))){
                    return true;
                }
            }
            if (StringUtils.isNotEmpty(jsonObject.getString("pictureUrlId"))){
                if (StringUtils.equals(fileUuid,jsonObject.getString("pictureUrlId"))){
                    return true;
                }
            }
            if (StringUtils.isNotEmpty(jsonObject.getString("videoUrlId"))){
                if (StringUtils.equals(fileUuid,jsonObject.getString("videoUrlId"))){
                    return true;
                }
            }
            if (StringUtils.isNotEmpty(jsonObject.getString("fileUrlId"))){
                if (StringUtils.equals(fileUuid,jsonObject.getString("fileUrlId"))){
                    return true;
                }
            }
        };
        //机器人流程中占用的素材id
        List<String> usedFileIds = robotProcessTreeApi.checkUsed();
        if (CollectionUtils.isEmpty(usedFileIds)){
            return false;
        }else {
            return usedFileIds.contains(fileUuid);
        }
    }

    /**
     * 统计素材数量接口
     * @param fileTidReq  运营商
     * @return 数量集合
     */
    public List<StatisticsResp> statistics(FileTidReq fileTidReq) {
        List<StatisticsResp> respList = new ArrayList<>();
        QueryWrapper wrapper = new QueryWrapper();
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId,superAdministratorUserIdConfigure.getSuperAdministrator())){
            wrapper.eq("creator",userId);
        }
        StatisticsResp audioStatistic = new StatisticsResp();
        StatisticsResp videoStatistic = new StatisticsResp();
        StatisticsResp pictureStatistic = new StatisticsResp();
        StatisticsResp upFileStatistic = new StatisticsResp();
        audioStatistic.setType(MaterialEnum.AUDIO.getCode());
        videoStatistic.setType(MaterialEnum.VIDEO.getCode());
        pictureStatistic.setType(MaterialEnum.PICTURE.getCode());
        upFileStatistic.setType(MaterialEnum.FILE.getCode());
        if (StringUtils.isEmpty(fileTidReq.getOperator())){
            //不带筛选条件
            //音频
            Long audioTotal = audioMapper.selectCount(wrapper);
            audioStatistic.setTotal(audioTotal);
            //视频
            Long videoTotal = videoMapper.selectCount(wrapper);
            videoStatistic.setTotal(videoTotal);
            //图片
            Long pictureTotal = pictureMapper.selectCount(wrapper);
            pictureStatistic.setTotal(pictureTotal);
            //文件
            Long upFileTotal = upFileMapper.selectCount(wrapper);
            upFileStatistic.setTotal(upFileTotal);
        }else {
            String operator = fileTidReq.getOperator();
            if (StringUtils.isNotEmpty(operator)){
                AccountManagementResp account = accountManagementApi.getAccountManagementByAccountId(operator);
                if (ObjectUtil.isNotEmpty(account.getAccountType())){
                    operator = account.getAccountType();
                }else {
                    operator = "null";
                }
            }
            //音频
            audioStatistic.setStatusTotals(examineResultMapper.countAudio(operator,userId));
            audioStatistic.setTotal(getTotal(examineResultMapper.countAudio(operator,userId)));
            //图片
            videoStatistic.setStatusTotals(examineResultMapper.countVideo(operator,userId));
            videoStatistic.setTotal(getTotal(examineResultMapper.countVideo(operator,userId)));
            //视频
            pictureStatistic.setStatusTotals(examineResultMapper.countPicture(operator,userId));
            pictureStatistic.setTotal(getTotal(examineResultMapper.countPicture(operator,userId)));
            //文件
            upFileStatistic.setStatusTotals(examineResultMapper.countFile(operator,userId));
            upFileStatistic.setTotal(getTotal(examineResultMapper.countFile(operator,userId)));
        }
        respList.add(audioStatistic);
        respList.add(videoStatistic);
        respList.add(pictureStatistic);
        respList.add(upFileStatistic);
        return respList;
    }

    private Long getTotal(List<StatusTotal> list){
        return list.stream().mapToLong(StatusTotal::getTotal).sum();
    }
}
