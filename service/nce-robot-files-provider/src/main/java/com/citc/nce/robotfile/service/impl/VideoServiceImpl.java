package com.citc.nce.robotfile.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.vo.tempStore.Csp4CustomerVideo;
import com.citc.nce.dto.PageReq;
import com.citc.nce.dto.VideoReq;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.DeleteReq;
import com.citc.nce.robotfile.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.robotfile.entity.ExamineResultDo;
import com.citc.nce.robotfile.entity.VideoDo;
import com.citc.nce.robotfile.exp.RobotFileExp;
import com.citc.nce.robotfile.mapper.ExamineResultMapper;
import com.citc.nce.robotfile.mapper.VideoMapper;
import com.citc.nce.robotfile.service.IExamineResultService;
import com.citc.nce.robotfile.service.IVideoService;
import com.citc.nce.vo.AccountResp;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.PageResultResp;
import com.citc.nce.vo.VideoResp;
import com.citc.nce.vo.tempStore.UpdateTid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: VideoServiceImpl
 */
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
@Slf4j
public class VideoServiceImpl extends ServiceImpl<VideoMapper, VideoDo> implements IVideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Resource
    private IExamineResultService examineResultService;

    @Resource
    private AccountManagementApi accountManagementApi;

    @Resource
    private ExamineResultMapper examineResultMapper;

    @Resource
    private FileApi fileApi;

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;


    @Override
    public VideoResp findOne(Long id) {
        VideoDo video = getById(id);
        if (Objects.isNull(video)) {
            throw new BizException("视频不存在");
        }
        if (!SessionContextUtil.getUser().getUserId().equals(video.getCreator())) {
            throw new BizException("你不能查看该视频");
        }
        LambdaQueryWrapper<ExamineResultDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamineResultDo::getFileUuid, video.getVideoUrlId());
        List<ExamineResultDo> examineResultDos = examineResultMapper.selectList(wrapper);
        List<AccountResp> accounts = new ArrayList<>();
        examineResultDos.forEach(e -> {
            AccountResp accountResp = new AccountResp();
            accountResp.setOperator(e.getOperator());
            accountResp.setStatus(e.getFileStatus());
            accounts.add(accountResp);
        });
        VideoResp videoResp = change(video);
        videoResp.setAccounts(accounts);
        return videoResp;
    }


    @Override
    public PageResultResp<VideoResp> selectByPage(PageReq req) {
        PageResultResp<VideoResp> pageResult = new PageResultResp<>();
        QueryWrapper<VideoDo> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("video_upload_time");
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        if (!StringUtils.isEmpty(req.getTabs())) {
            if (!CollectionUtil.isEmpty(req.getMaterialIds())) {
                wrapper.in("id", req.getMaterialIds());
            } else {
                pageResult.setTotal(0L);
                pageResult.setList(new ArrayList<>());
                return pageResult;
            }
        }
        List<VideoDo> records = videoMapper.selectList(wrapper);
        List<VideoResp> vos = new ArrayList<>();
        List<AccountManagementResp> list = accountManagementApi.getAccountManagementlist(userId);
        List<String> fileUuids = records.stream().map(VideoDo::getMainCoverId).collect(Collectors.toList());
        Map<String, String> autoThumbnailMap = fileApi.getAutoThumbnail(fileUuids);
        for (VideoDo video : records) {
            VideoResp vo = change(video);
            String videoUrlId = vo.getVideoUrlId();
            List<AccountResp> accountList = examineResultService.getAccountList(req, videoUrlId, list);
            // 将accounts和预置的四个通道运营商的取交集，默认填充交集的数据，状态设置为未审核
            fillAccounts(accountList);
            vo.setAccounts(accountList);
            vo.setMainCoverThumbnail(autoThumbnailMap.get(vo.getMainCoverId()));
            vos.add(vo);
        }
        List<VideoResp> result = filterResult(vos, req);
        result = filterAllAccount(result, req);
        List<VideoResp> newResult = result.stream().skip((long) (req.getPageNo() - 1) * req.getPageSize())
                .limit(req.getPageSize()).collect(Collectors.toList());
        pageResult.setTotal((long) result.size());
        if (result.size() == 0) {
            pageResult.setTotal(0L);
        }
        pageResult.setList(newResult);
        return pageResult;
    }

    private void fillAccounts(List<AccountResp> accounts) {
        List<String> baseOperatorList = new ArrayList<>(Arrays.asList("硬核桃", "移动", "电信", "联通"));
        List<String> operatorList = accounts.stream().map(AccountResp::getOperator).collect(Collectors.toList());
        List<String> diff = baseOperatorList.stream().filter(item -> !operatorList.contains(item)).collect(Collectors.toList());
        diff.forEach(operator -> {
            AccountResp temp = new AccountResp();
            temp.setOperator(operator);
            temp.setStatus(1); // 待审核
            accounts.add(temp);
        });
    }


    private List<VideoResp> filterResult(List<VideoResp> list, PageReq pageReq) {
        //筛选文件名
        List<VideoResp> fileNameList = filterFileName(list, pageReq.getFileName());
        //筛选账号
        List<VideoResp> accountList = filterAccount(fileNameList, pageReq.getChatbotName());
        //筛选状态
        return filterStatus(accountList, pageReq);
    }

    private List<VideoResp> filterStatus(List<VideoResp> accountList, PageReq pageReq) {
        List<VideoResp> result = new ArrayList<>();
        if (null == pageReq.getStatus() || pageReq.getStatus() == 0) {
            return accountList;
        }
        for (VideoResp videoResp : accountList) {
            // 查询状态的同时，如果chatbotName不为空，需要过滤
            List<AccountResp> collect;
            if (StringUtils.isEmpty(pageReq.getChatbotName())) {
                collect = videoResp.getAccounts().stream().filter(
                        account -> Objects.equals(pageReq.getStatus(), account.getStatus())
                ).collect(Collectors.toList());
            } else {
                collect = videoResp.getAccounts().stream().filter(
                        account -> Objects.equals(account.getStatus(), pageReq.getStatus()) &&
                                StringUtils.equals(account.getOperator(), pageReq.getChatbotName())
                ).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(collect)) {
                result.add(videoResp);
            }
        }
        return result;
    }

    private List<VideoResp> filterAccount(List<VideoResp> fileNameList, String chatbotName) {
        List<VideoResp> result = new ArrayList<>();
        if (!StringUtils.isNotBlank(chatbotName)) {
            return fileNameList;
        }
        for (VideoResp videoResp : fileNameList) {
            List<AccountResp> collect = new ArrayList<>();
            if (null != videoResp.getAccounts()) {
                collect = videoResp.getAccounts().stream().filter(account -> StringUtils.equals(account.getOperator(), chatbotName)).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(collect)) {
                result.add(videoResp);
            }
        }
        return result;
    }

    private List<VideoResp> filterFileName(List<VideoResp> list, String fileName) {
        if (!StringUtils.isNotBlank(fileName)) {
            return list;
        }
        return list.stream().filter(videoResp -> videoResp.getVideoName().contains(fileName)).collect(Collectors.toList());
    }

    private List<VideoResp> filterAllAccount(List<VideoResp> result, PageReq pageReq) {
        List<VideoResp> finalResult = new ArrayList<>();
        if (null != pageReq.getOperators() && pageReq.getOperators().size() == 0) {
            return finalResult;
        }
        if (null == pageReq.getOperators()) {
            return result;
        }
        for (VideoResp videoResp : result) {
            // List<String> collect = videoResp.getAccounts().stream().map(AccountResp::getOperator).collect(Collectors.toList());
            //videoResp.getAccounts() 一定包含了4个通道的状态，前面步骤做了每个通道状态填充，filter后一定包含pageReq.getOperators()所有通道
            boolean statusResult = videoResp.getAccounts().stream().filter(item -> pageReq.getOperators().contains(item.getOperator())).allMatch(e -> e.getStatus() == 2);
            if (statusResult) {
                finalResult.add(videoResp);
            }
        }
        return finalResult;
    }

    @Override
    @Transactional
    public void saveVideo(VideoReq dto) {
        QueryWrapper<VideoDo> wrapper = new QueryWrapper<>();
        wrapper.eq("video_url_id", dto.getVideoUrlId());
        VideoDo exit = videoMapper.selectOne(wrapper);
        if (exit == null) {
            VideoDo video = new VideoDo();
            List<String> covers = dto.getCovers();
            String strip = StringUtils.strip(covers.toString(), "[]");
            BeanUtils.copyProperties(dto, video);
            video.setCropObj(JSONObject.toJSONString(dto.getCropObj()));
            video.setCoverUrlIds(strip);
            video.setVideoUploadTime(new Date());
            save(video);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVideoList(List<VideoReq> req) {
        if (CollectionUtils.isEmpty(req)) {
            return;
        }
        List<String> urlIdList = new ArrayList<>();
        HashMap<String, VideoDo> insertMap = new HashMap<>();
        for (VideoReq videoReq :
                req) {
            urlIdList.add(videoReq.getVideoUrlId());
            VideoDo video = new VideoDo();
            List<String> covers = videoReq.getCovers();
            String strip = StringUtils.strip(covers.toString(), "[]");
            BeanUtils.copyProperties(videoReq, video);
            video.setCropObj(JSONObject.toJSONString(videoReq.getCropObj()));
            video.setCoverUrlIds(strip);
            video.setVideoUploadTime(new Date());
            video.setDeleted(0);
            video.setDeleteTime(new Date());
            video.setUsed(0);
            insertMap.put(videoReq.getVideoUrlId(), video);
        }

        // 检查是否有重复的
        try {
            QueryWrapper<VideoDo> wrapper = new QueryWrapper<>();
            wrapper.in("video_url_id", urlIdList);
            List<VideoDo> videoDos = videoMapper.selectList(wrapper);
            // 去重
            if (CollectionUtils.isNotEmpty(videoDos)) {
                videoDos.stream().forEach(videoDo -> insertMap.remove(videoDo.getVideoUrlId()));
            }
            Iterator<Map.Entry<String, VideoDo>> iterator = insertMap.entrySet().iterator();
            List<VideoDo> insertList = new ArrayList<>();
            while (iterator.hasNext()) {
                insertList.add(iterator.next().getValue());
            }
            if (CollectionUtils.isNotEmpty(insertList)) {
                videoMapper.insertBatch(insertList);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
//            throw new BizException(RobotFileExp.SQL_ERROR);
        }
    }

    @Override
    @Transactional
    public Map<Long, Csp4CustomerVideo> saveVideoListCsp4CustomerVideo(List<Csp4CustomerVideo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        //用户收集新id
        Map<Long, Csp4CustomerVideo> returnMap = list.stream()
                .collect(Collectors.toMap(Csp4CustomerVideo::getVideoId, Function.identity()));

        Map<String, VideoDo> insertMap = list.stream().map(s -> {
            VideoDo video = new VideoDo();
            List<String> covers = s.getCovers();
            String strip = StringUtils.strip(covers.toString(), "[]");
            BeanUtils.copyProperties(s, video);

            video.setVideoUploadTime(new Date());
            video.setDeleted(0);
            video.setDeleteTime(new Date());
            video.setUsed(0);
            video.setVideoSize(s.getSize());
            video.setVideoFormat(s.getFormat());
            video.setVideoName(s.getName());
            video.setVideoUrlId(s.getFileId());
            video.setVideoDuration(s.getDuration());
            video.setMainCoverId(s.getCover());
            video.setThumbnailTid(s.getCover());
            video.setMainCoverBackId(covers.get(0));
            video.setCropObj(s.getCropObj());
            video.setCoverUrlIds(strip);
            //备份原始id
            video.setOldId(s.getVideoId());
            return video;
        }).collect(Collectors.toMap(VideoDo::getVideoUrlId, Function.identity()));


        List<VideoDo> insertList = new ArrayList<>(insertMap.values());
        if (CollectionUtils.isNotEmpty(insertList)) {
            insertList.forEach(s -> videoMapper.insert(s));
        }

        //回写新id;
        for (VideoDo videoDo : insertList) {
            Csp4CustomerVideo entity = returnMap.get(videoDo.getOldId());
            if (Objects.isNull(entity)) {
                log.warn("Csp4CustomerVideo lost file , oldId: {}", videoDo.getOldId());
                continue;
            }
            entity.setNewId(videoDo.getId());
        }
        return returnMap;
    }

    @Override
    public void updateTid(List<UpdateTid> updateTidList) {
        for (UpdateTid updateTid : updateTidList) {
            update(new LambdaUpdateWrapper<VideoDo>()
                    .set(VideoDo::getThumbnailTid, updateTid.getUploadResp().getThumbnailTid())
                    .eq(VideoDo::getId, updateTid.getId()));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateVideoThumbnail(String videoUrlId, String thumbnailId) {
        lambdaUpdate()
                .eq(VideoDo::getVideoUrlId, videoUrlId)
                .set(VideoDo::getThumbnailTid, thumbnailId)
                .update();
    }

    @Override
    @Transactional
    public void updateVideo(VideoReq dto) {
        VideoDo videoDo = getById(dto.getId());
        if (Objects.isNull(videoDo)) {
            throw new BizException("视频不存在");
        }
        if (!SessionContextUtil.getUser().getUserId().equals(videoDo.getCreator())) {
            throw new BizException("不能修改别人的视频");
        }
        UpdateWrapper<VideoDo> wrapper = new UpdateWrapper<>();
        LambdaUpdateWrapper<ExamineResultDo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ExamineResultDo::getFileUuid, dto.getVideoUrlId());
        updateWrapper.set(ExamineResultDo::getFileStatus, 1);
        examineResultService.update(updateWrapper);
        if (dto.getVideoName() != null) {
            wrapper.set("video_name", dto.getVideoName());
        }
        if (dto.getMainCoverId() != null) {
            wrapper.set("main_cover_id", dto.getMainCoverId());
        }
        if (StringUtils.isNotEmpty(dto.getThumbnailTid())) {
            wrapper.set("thumbnail_tid", dto.getThumbnailTid());
        }
        wrapper.set("cover_url_ids", StringUtils.strip(dto.getCovers().toString(), "[]"));
        wrapper.eq("id", dto.getId());
        wrapper.set("main_cover_back_id", dto.getMainCoverBackId());
        wrapper.set("crop_obj", JSONObject.toJSONString(dto.getCropObj()));
        if (!update(wrapper)) throw new BizException(RobotFileExp.SQL_ERROR);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResp deleteVideo(Long id) {
        VideoDo videoDo = videoMapper.selectById(id);
        if (Objects.isNull(videoDo)) {
            throw new BizException("视频不存在");
        }
        if (!SessionContextUtil.getUser().getUserId().equals(videoDo.getCreator())) {
            throw new BizException("你不能删除别人的视频");
        }
        String videoUrlId = videoDo.getVideoUrlId();
        String mainCoverId = videoDo.getMainCoverId();
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        int count = videoMapper.logicDeleteByIds(ids);
        if (count <= 0) throw new BizException(RobotFileExp.SQL_ERROR);
        String urlIds = videoDo.getCoverUrlIds();
        String[] split = urlIds.split(",");
        List<String> result = new ArrayList<>(Arrays.asList(split));
        result.add(videoUrlId);
        result.add(mainCoverId);
        result.add(videoDo.getThumbnailTid());
        result.add(videoDo.getMainCoverBackId());
        DeleteResp resp = new DeleteResp();
        resp.setFileUrlIds(result);
        DeleteReq deleteReq = new DeleteReq();
        deleteReq.setFileUrlIds(result);
        fileApi.deleteFile(deleteReq);
        return resp;
    }

    private VideoResp change(VideoDo video) {
        VideoResp vo = new VideoResp();
        BeanUtils.copyProperties(video, vo);
        String urlIds = video.getCoverUrlIds();
        String[] split = urlIds.split(",");
        List<String> covers = Arrays.asList(split);
        vo.setCovers(covers);
        String cropObj = video.getCropObj();
        JSONObject jsonObject = JSONObject.parseObject(cropObj, JSONObject.class);
        vo.setCropObj(jsonObject);
        return vo;
    }

    @Override
    public VideoResp findOneByUuid(String videoUrlId) {
        LambdaQueryWrapper<VideoDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoDo::getVideoUrlId, videoUrlId);
        VideoDo videoDo = videoMapper.selectOne(wrapper);
        if (Optional.ofNullable(videoDo).isPresent()) {
            return change(videoDo);
        } else {
            return new VideoResp();
        }
    }
}
