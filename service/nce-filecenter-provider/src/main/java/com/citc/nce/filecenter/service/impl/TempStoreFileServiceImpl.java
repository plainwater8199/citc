package com.citc.nce.filecenter.service.impl;

import cn.hutool.core.util.StrUtil;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTypeReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.vo.tempStore.Csp4CustomerAudio;
import com.citc.nce.common.vo.tempStore.Csp4CustomerImg;
import com.citc.nce.common.vo.tempStore.Csp4CustomerVideo;
import com.citc.nce.fileApi.AudioApi;
import com.citc.nce.fileApi.PictureApi;
import com.citc.nce.fileApi.VideoApi;
import com.citc.nce.filecenter.service.FileManageService;
import com.citc.nce.filecenter.service.TempStoreFileService;
import com.citc.nce.filecenter.tempStore.*;
import com.citc.nce.filecenter.vo.UploadReq;
import com.citc.nce.filecenter.vo.UploadResp;
import com.citc.nce.vo.tempStore.UpdateTid;
import io.seata.core.context.RootContext;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Component
@Slf4j
@AllArgsConstructor
public class TempStoreFileServiceImpl implements TempStoreFileService {
    private final ConcurrentHashMap<Long, MaterialCache> rollbackMap = new ConcurrentHashMap<>();

    private final PictureApi pictureApi;
    private final AudioApi audioApi;
    private final VideoApi videoApi;
    private final FileManageService fileService;
    private final AccountManagementApi accountManagementApi;


    @Data
    private static class MaterialCache {
        @ApiModelProperty(value = "关联5G消息账号 chatbotAccount")
        private String[] chatbotAccount;
        @ApiModelProperty(value = "用户")
        private String userId;
        @ApiModelProperty(value = "上传文件结果")
        private SaveMaterialResult result;
    }


    @Override
    public SaveMaterialResult saveTempStoreMaterial(SaveMaterialReq saveMaterialReq) {
        SaveMaterialResult result = new SaveMaterialResult();
        result.setThreadId(Thread.currentThread().getId());
        if (!CollectionUtils.isEmpty(saveMaterialReq.getAudioList())) {
            result.setAudioMap(saveMaterialAudio(saveMaterialReq.getAudioList(), saveMaterialReq.getUserId()));
        }

        if (!CollectionUtils.isEmpty(saveMaterialReq.getPictureList())) {
            result.setImgMap(saveMaterialImg(saveMaterialReq.getPictureList(), saveMaterialReq.getUserId()));
        }

        if (!CollectionUtils.isEmpty(saveMaterialReq.getVideoList())) {
            result.setVideoMap(saveMaterialVideo(saveMaterialReq.getVideoList(), saveMaterialReq.getUserId()));
        }

        //保存记录到缓存
        String xid = RootContext.getXID();
        if (StringUtils.hasLength(xid)) {
            MaterialCache cache = new MaterialCache();
            cache.setResult(result);
            cache.setChatbotAccount(saveMaterialReq.getChatbotAccount());
            cache.setUserId(saveMaterialReq.getUserId());
            rollbackMap.put(result.getThreadId(), cache);
        }
        return result;
    }


    @Override
    public void commit(Long mapKey) {
        MaterialCache cache = rollbackMap.remove(mapKey);
        if (Objects.isNull(cache)) return;

        SaveMaterialResult result = cache.getResult();
        //素材送审

        Map<Long, Csp4CustomerAudio> audioMap = result.getAudioMap();
        if (!CollectionUtils.isEmpty(audioMap)) {
            List<String> list = audioMap.values().stream().map(Csp4CustomerAudio::getFileId).collect(Collectors.toList());
            List<UpdateTid> examine = examine(cache, list, 1);//类型从前段页面上来的
            //音频表中没有记录tid
            log.info("audioMap {}  examine {}", audioMap, examine);
        }


        Map<Long, Csp4CustomerImg> imgMap = result.getImgMap();
        if (!CollectionUtils.isEmpty(imgMap)) {
            List<String> list = imgMap.values().stream().map(Csp4CustomerImg::getPictureUrlid).collect(Collectors.toList());
            List<UpdateTid> examine = examine(cache, list, 3);//类型从前段页面上来的
            Map<String, Long> map = result.getImgMap().values().stream()
                    .collect(Collectors.toMap(Csp4CustomerImg::getPictureUrlid, Csp4CustomerImg::getNewId));
            examine.forEach(s -> s.setId(map.get(s.getFileUUid())));
            log.info("imgMap {}  examine {}", imgMap, examine);
            pictureApi.updateTid(examine);
        }


        Map<Long, Csp4CustomerVideo> videoMap = result.getVideoMap();
        if (!CollectionUtils.isEmpty(videoMap)) {
            List<String> list = videoMap.values().stream().map(Csp4CustomerVideo::getFileId).collect(Collectors.toList());
            List<UpdateTid> examine = examine(cache, list, 4);//类型从前段页面上来的
            Map<String, Long> map = result.getVideoMap().values().stream()
                    .collect(Collectors.toMap(Csp4CustomerVideo::getFileId, Csp4CustomerVideo::getNewId));
            examine.forEach(s -> s.setId(map.get(s.getFileUUid())));
            videoApi.updateTid(examine);
            log.info("videoMap {}  examine {}", videoMap, examine);
        }

    }

    private List<UpdateTid> examine(MaterialCache cache, List<String> fileUUIDs, int mediaType) {
        if (CollectionUtils.isEmpty(fileUUIDs)) return new ArrayList<>();
        if (Objects.isNull(cache.getChatbotAccount()) || cache.getChatbotAccount().length < 1) return new ArrayList<>();


        List<UpdateTid> respList = new ArrayList<>(fileUUIDs.size());
        for (String chatbotAccount : cache.getChatbotAccount()) {
            AccountManagementTypeReq typeReq = new AccountManagementTypeReq();
            typeReq.setCreator(cache.getUserId());
            typeReq.setChatbotAccount(chatbotAccount);
            AccountManagementResp account = accountManagementApi.getAccountManagementByAccountType(typeReq);
            if (account == null) {
                throw new BizException("机器人账号未找到");
            }
            for (String fileUUID : fileUUIDs) {
                try {
                    UploadResp uploadResp = fileService.examineItem(fileUUID, account, mediaType);
                    UpdateTid tempStoreUpdateTid = new UpdateTid();
                    tempStoreUpdateTid.setFileUUid(fileUUID);
                    tempStoreUpdateTid.setUploadResp(uploadResp);
                    respList.add(tempStoreUpdateTid);
                } catch (Exception e) {
                    log.error("模板商城异步送审失败, fileUUID {} account {}", fileUUID, account, e);
                }

            }
        }
        return respList;
    }

    @Override
    public void cancel(Long mapKey) {
        rollbackMap.remove(mapKey);
    }

    @Override
    public String saveTempStoreCardStyleImg(String oldFileUUid) {
        CommonsMultipartFile multipartFile = fileUuidToMultipartFile(oldFileUUid, null);
        UploadReq uploadReq = new UploadReq();
        uploadReq.setFile(multipartFile);
        uploadReq.setSceneId("codeincodeservice");
        return fileService.uploadFile(uploadReq, "robot").get(0).getUrlId();
    }

    private Map<Long, Csp4CustomerVideo> saveMaterialVideo(List<ResourcesVideo> videoList, String userId) {
        if (CollectionUtils.isEmpty(videoList)) return new HashMap<>();
        List<Csp4CustomerVideo> list = new ArrayList<>(videoList.size());
        for (ResourcesVideo video : videoList) {
            //备选封面图
            for (int i = 0; i < video.getCovers().size(); i++) {
                String cover = video.getCovers().get(i);
                UploadResp uploadResp = uploadFile(userId, cover, video.getName() + "cover" + i + ".jpeg", null);
                video.getCovers().set(i, uploadResp.getUrlId());
            }
            //主封面
            UploadResp mainCoverUploadResp = uploadFile(userId, video.getCover(), video.getName() + "mainCover.jpeg", null);
            UploadResp uploadResp = uploadFile(userId, video.getFileId(), video.getName(), mainCoverUploadResp.getUrlId());

            //保持视频资源表
            Csp4CustomerVideo csp4CustomerVideo = new Csp4CustomerVideo();
            BeanUtils.copyProperties(video, csp4CustomerVideo);
            csp4CustomerVideo.setFileId(uploadResp.getUrlId())
                    .setName(uploadResp.getFileName())
                    .setCover(mainCoverUploadResp.getUrlId())
                    .setSize(uploadResp.getFileSize())
                    .setDuration(uploadResp.getFileDuration())
                    .setFormat(uploadResp.getFileFormat());
            list.add(csp4CustomerVideo);
        }
        return videoApi.saveVideoList(list);
    }


    private Map<Long, Csp4CustomerAudio> saveMaterialAudio(List<ResourcesAudio> audioList, String userId) {
        if (CollectionUtils.isEmpty(audioList)) return new HashMap<>();
        List<Csp4CustomerAudio> list = new ArrayList<>(audioList.size());
        //上传和送审文件
        for (ResourcesAudio audio : audioList) {
            UploadResp uploadResp = uploadFile(userId, audio.getFileId(), audio.getName(), null);
            Csp4CustomerAudio csp4CustomerAudio = new Csp4CustomerAudio();
            BeanUtils.copyProperties(audio, csp4CustomerAudio);
            //送审后的数据设置
            csp4CustomerAudio.setFileId(uploadResp.getUrlId())
                    .setName(uploadResp.getFileName())
                    .setSize(uploadResp.getFileSize())
                    .setFormat(uploadResp.getFileFormat());
            list.add(csp4CustomerAudio);
        }
        //保存文件到客户素材
        return audioApi.saveAudioList(list);
    }


    private Map<Long, Csp4CustomerImg> saveMaterialImg(List<ResourcesImg> pictureList, String userId) {
        if (CollectionUtils.isEmpty(pictureList)) return new HashMap<>();
        List<Csp4CustomerImg> list = new ArrayList<>(pictureList.size());
        for (ResourcesImg img : pictureList) {
            UploadResp uploadResp = uploadFile(userId, img.getPictureUrlid(), img.getPictureName(), null);
            Csp4CustomerImg csp4CustomerImg = new Csp4CustomerImg();
            BeanUtils.copyProperties(img, csp4CustomerImg);
            csp4CustomerImg.setPictureUrlid(uploadResp.getUrlId())
                    .setPictureName(uploadResp.getFileName())
                    .setThumbnailTid(uploadResp.getThumbnailTid())
                    .setPictureSize(uploadResp.getFileSize())
                    .setPictureFormat(uploadResp.getFileFormat());
            list.add(csp4CustomerImg);
        }
        return pictureApi.savePictureList(list);
    }


    /**
     * 上传并送审到运营商
     *
     * @param creator       创建人ID
     * @param fileUuid      文件ID
     * @param filename      文件名称
     * @param thumbnailUuid 缩略图ID
     */
    private UploadResp uploadFile(String creator, String fileUuid, String filename, String thumbnailUuid) {

        UploadReq uploadReq = new UploadReq();
        uploadReq.setCreator(creator);
        uploadReq.setList(null); //不送网关，等导入完成后再送审网关
        uploadReq.setFile(fileUuidToMultipartFile(fileUuid, filename));
        if (StringUtils.hasText(thumbnailUuid)) {
            //下载并转换缩略图
            uploadReq.setThumbnail(fileUuidToMultipartFile(thumbnailUuid, filename + "-Thumbnail.jpeg"));
        }
        uploadReq.setSceneId("codeincodeservice");
        List<UploadResp> uploaded = fileService.uploadFile(uploadReq, "robot");

        return uploaded.get(0);
    }

    private CommonsMultipartFile fileUuidToMultipartFile(String fileUuid, String filename) {
        ResponseEntity<byte[]> fileEntity = fileService.downloadFile(fileUuid);
        //文件名不传时从响应头中获取原始的
        if (StrUtil.isBlankIfStr(filename)) {
            try {
                List<String> strings = fileEntity.getHeaders().get("Content-Disposition");
                Assert.isTrue(strings != null && !strings.isEmpty(), " filename cannot be empty");
                filename = URLEncoder.encode(strings.get(0).replace("attachment; filename=", ""), "UTF-8");
            } catch (UnsupportedEncodingException ignored) {
            }
        }
        FileItem fileItem = new DiskFileItemFactory().createItem("file"
                , MediaType.MULTIPART_FORM_DATA_VALUE
                , true
                , filename
        );
        try (InputStream input = new ByteArrayInputStream(Objects.requireNonNull(fileEntity.getBody()));
             OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);
        } catch (Exception ignored) {
        }
        return new CommonsMultipartFile(fileItem);
    }


}
