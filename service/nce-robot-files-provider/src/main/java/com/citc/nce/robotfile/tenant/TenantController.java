package com.citc.nce.robotfile.tenant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.authcenter.userDataSyn.vo.TenantUserUpdateInfo;
import com.citc.nce.fileApi.TenantApi;
import com.citc.nce.robotfile.entity.*;
import com.citc.nce.robotfile.mapper.*;
import com.citc.nce.vo.tenant.req.TenantUpdateInfoReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class TenantController implements TenantApi {

    @Resource
    private AudioMapper audioMapper; //tb_audio
    @Resource
    private GroupMapper groupMapper; //tb_group
    @Resource
    private PictureMapper pictureMapper; //tb_picture
    @Resource
    private VideoMapper videoMapper; //tb_video

    @Resource
    private ExamineResultMapper examineResultMapper;//tb_examine_result

    @Resource
    private AvailableQuantityMapper availableQuantityMapper;//tb_available_quantity





    @Override
    public void updateData(TenantUpdateInfoReq req) {
        List<TenantUserUpdateInfo> tenantUserUpdateInfos = req.getTenantUserUpdateInfos();
        if(!CollectionUtils.isEmpty(tenantUserUpdateInfos)){
            System.out.println("-------------------rebot-file-数据更新执行------------------");
            for(TenantUserUpdateInfo userUpdateInfo : tenantUserUpdateInfos){
                String customerUserId = userUpdateInfo.getCustomerUserId();
                String customerId = userUpdateInfo.getCustomerId();
                Map<String, String> chatbotAccountIdMap = userUpdateInfo.getChatbotAccountIdMap();



                //tb_examine_result
                LambdaQueryWrapper<ExamineResultDo> examineResultDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
                examineResultDoLambdaQueryWrapper.eq(ExamineResultDo::getCreator,customerUserId).or().eq(ExamineResultDo::getCreator,customerId);//customer的userId
                List<ExamineResultDo> examineResultDoList = examineResultMapper.selectList(examineResultDoLambdaQueryWrapper);
                List<ExamineResultDo> examineResultDos = new ArrayList<>();
                for(ExamineResultDo item : examineResultDoList){
                    item.setCreator(customerId);
                    item.setChatbotAccountId(chatbotAccountIdMap.getOrDefault(item.getChatbotId(),""));
                    examineResultDos.add(item);
                }
                if(!CollectionUtils.isEmpty(examineResultDos)){
                    examineResultMapper.updateBatch(examineResultDos);
                }

                //tb_available_quantity
                LambdaQueryWrapper<AvailableQuantityDo> availableQuantityDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
                availableQuantityDoLambdaQueryWrapper.eq(AvailableQuantityDo::getCreator,customerUserId);//customer的userId
                List<AvailableQuantityDo> availableQuantityDos = availableQuantityMapper.selectList(availableQuantityDoLambdaQueryWrapper);
                List<AvailableQuantityDo> availableQuantityDoList = new ArrayList<>();
                for(AvailableQuantityDo item : availableQuantityDos){
                    item.setCreator(customerId);
                    availableQuantityDoList.add(item);
                }
                if(!CollectionUtils.isEmpty(availableQuantityDoList)){
                    availableQuantityMapper.updateBatch(availableQuantityDoList);
                }
                //tb_group
                LambdaQueryWrapper<GroupDo> groupDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
                groupDoLambdaQueryWrapper.eq(GroupDo::getCreator,customerUserId);//customer的userId
                List<GroupDo> groupDoList = groupMapper.selectList(groupDoLambdaQueryWrapper);
                List<GroupDo> groupDos = new ArrayList<>();
                for(GroupDo item : groupDoList){
                    item.setCreator(customerId);
                    groupDos.add(item);
                }
                if(!CollectionUtils.isEmpty(groupDos)){
                    groupMapper.updateBatch(groupDos);
                }

                //tb_video
                LambdaQueryWrapper<VideoDo> videoDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
                videoDoLambdaQueryWrapper.eq(VideoDo::getCreator,customerUserId);//customer的userId
                List<VideoDo> videoDoList = videoMapper.selectList(videoDoLambdaQueryWrapper);
                List<VideoDo> videoDos = new ArrayList<>();
                for(VideoDo item : videoDoList){
                    item.setCreator(customerId);
                    videoDos.add(item);
                }
                if(!CollectionUtils.isEmpty(videoDos)){
                    videoMapper.updateBatch(videoDos);
                }


                //tb_picture
                LambdaQueryWrapper<PictureDo> pictureDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
                pictureDoLambdaQueryWrapper.eq(PictureDo::getCreator,customerUserId);//customer的userId
                List<PictureDo> pictureDoList = pictureMapper.selectList(pictureDoLambdaQueryWrapper);
                List<PictureDo> pictureDos = new ArrayList<>();
                for(PictureDo item : pictureDoList){
                    item.setCreator(customerId);
                    pictureDos.add(item);
                }
                if(!CollectionUtils.isEmpty(pictureDos)){
                    pictureMapper.updateBatch(pictureDos);
                }

                //tb-audio
                LambdaQueryWrapper<AudioDo> audioDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
                audioDoLambdaQueryWrapper.eq(AudioDo::getCreator,customerUserId);//customer的userId
                List<AudioDo> audioDoList = audioMapper.selectList(audioDoLambdaQueryWrapper);
                List<AudioDo> audioDos = new ArrayList<>();
                for(AudioDo item : audioDoList){
                    item.setCreator(customerId);
                    audioDos.add(item);
                }
                if(!CollectionUtils.isEmpty(audioDos)){
                    audioMapper.updateBatch(audioDos);
                }

            }
        }

        log.info("--------------------------------file服务数据同步完成--------------------------------");




    }

    @Override
    public void updateChatbot(TenantUpdateInfoReq req) {
        List<ExamineResultDo> examineResultDoList = examineResultMapper.selectList();
        List<ExamineResultDo> update = new ArrayList<>();
        Map<Long, String> chatbotId = req.getChatbotId();
        for(ExamineResultDo item : examineResultDoList){
            if(chatbotId.containsKey(item.getAccountId())){
                item.setChatbotAccountId(chatbotId.get(item.getAccountId()));
                update.add(item);
            }
        }
        if(!CollectionUtils.isEmpty(update)){
            examineResultMapper.updateBatch(update);
        }
    }
}
