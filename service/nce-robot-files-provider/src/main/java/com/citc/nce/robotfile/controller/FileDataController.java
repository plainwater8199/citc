package com.citc.nce.robotfile.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.fileApi.FileDataApi;
import com.citc.nce.robotfile.entity.*;
import com.citc.nce.robotfile.mapper.*;
import com.citc.nce.vo.UpdateDateReq;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: AudioController
 */
@Api(value = "音频管理")
@RestController
public class FileDataController implements FileDataApi {

    @Resource
    private AudioMapper audioMapper;

    @Resource
    private PictureMapper pictureMapper;
    @Resource
    private ExamineResultMapper examineResultMapper;
    @Resource
    private GroupMapper groupMapper;
    @Resource
    private UpFileMapper upFileMapper;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private FileManageMapper fileManageMapper;





    @Override
    @PostMapping(value = "/tenant/updateData")
    public void updateData(@RequestBody UpdateDateReq req) {
        String customerId = req.getCustomerId();
        String cspId = req.getCspId();
        String customerUserId = req.getCustomerUserId();
        String cspUserId = req.getCspUserId();
        Map<Long, String> accountManagementIdMap = req.getAccountManagementIdMap();
        Map<Long, String> cspAccountManageMap = req.getCspAccountManageMap();



        //aim_order -- customerId
        LambdaQueryWrapper<AudioDo> audioDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        audioDoLambdaQueryWrapper.eq(AudioDo::getCreator,customerUserId);//customer的userId
        List<AudioDo> audioDos = audioMapper.selectList(audioDoLambdaQueryWrapper);
        for(AudioDo item : audioDos){
            item.setCreator(customerId);
            audioMapper.updateById(item);
        }


        LambdaQueryWrapper<ExamineResultDo> examineResultDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        examineResultDoLambdaQueryWrapper.eq(ExamineResultDo::getCreator,customerUserId);//customer的userId
        List<ExamineResultDo> examineResultDos = examineResultMapper.selectList(examineResultDoLambdaQueryWrapper);
        for(ExamineResultDo item : examineResultDos){
            item.setCreator(customerId);
            item.setChatbotAccountId(accountManagementIdMap.getOrDefault(item.getAccountId(),""));
            examineResultMapper.updateById(item);
        }


        LambdaQueryWrapper<GroupDo> groupDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        groupDoLambdaQueryWrapper.eq(GroupDo::getCreator,customerUserId);//customer的userId
        List<GroupDo> groupDos = groupMapper.selectList(groupDoLambdaQueryWrapper);
        for(GroupDo item : groupDos){
            item.setCreator(customerId);
            groupMapper.updateById(item);
        }


        LambdaQueryWrapper<UpFileDo> upFileDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        upFileDoLambdaQueryWrapper.eq(UpFileDo::getCreator,customerUserId);//customer的userId
        List<UpFileDo> upFileDos = upFileMapper.selectList(upFileDoLambdaQueryWrapper);
        for(UpFileDo item : upFileDos){
            item.setCreator(customerId);
            upFileMapper.updateById(item);
        }


        LambdaQueryWrapper<VideoDo> videoDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        videoDoLambdaQueryWrapper.eq(VideoDo::getCreator,customerUserId);//customer的userId
        List<VideoDo> videoDos = videoMapper.selectList(videoDoLambdaQueryWrapper);
        for(VideoDo item : videoDos){
            item.setCreator(customerId);
            videoMapper.updateById(item);
        }


        LambdaQueryWrapper<PictureDo> pictureDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pictureDoLambdaQueryWrapper.eq(PictureDo::getCreator,customerUserId);//customer的userId
        List<PictureDo> pictureDos = pictureMapper.selectList(pictureDoLambdaQueryWrapper);
        for(PictureDo item : pictureDos){
            item.setCreator(customerId);
            pictureMapper.updateById(item);
        }

        LambdaQueryWrapper<FileManageDo> fileManageDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileManageDoLambdaQueryWrapper.eq(FileManageDo::getCreator,customerUserId);//customer的userId
        List<FileManageDo> fileManageDos = fileManageMapper.selectList(fileManageDoLambdaQueryWrapper);
        for(FileManageDo item : fileManageDos){
            item.setCreator(customerId);
            fileManageMapper.updateById(item);
        }




    }
}
