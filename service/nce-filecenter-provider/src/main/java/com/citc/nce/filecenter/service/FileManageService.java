package com.citc.nce.filecenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTypeReq;
import com.citc.nce.filecenter.entity.FileManage;
import com.citc.nce.filecenter.platform.vo.UpReceiveData;
import com.citc.nce.filecenter.vo.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月18日15:12:01
 * @Version: 1.0
 * @Description: FileManageService
 */
@Service
public interface FileManageService extends IService<FileManage> {

    List<UploadResp> simpleUploadFile(UploadReq uploadReq, String bucketName);

    List<UploadResp> uploadFile(UploadReq uploadReq, String bucketName);

    ResponseEntity<byte[]> downloadFile(String uuid);

    void deleteFile(DeleteReq req);

    List<UploadResp> examine(ExamineReq examineReq);

    UploadResp examineOne(ExamineOneReq examineOneReq);

    UpReceiveData upToConsumer(UploadReq uploadReq);

    ResponseEntity<byte[]> downloadFile2(String req, HttpServletRequest request);

    FileInfo getFileInfo(String uuid);

    ResponseEntity<byte[]> videoDownload(DownloadReq downloadReq);

    String getToken(String appId, String appKey);

    /**
     * 素材送网关审核
     *
     * @param uuid              文件uuid
     * @param accountManagement 机器人信息
     * @param mediaType         媒体类型 1音频 3图片 4视频
     * @return  UploadResp
     */
    UploadResp examineItem(String uuid, AccountManagementResp accountManagement, Integer mediaType);

    FileInfo getFileInfoByScenarioId(FileInfoReq fileInfoReq);

    void checkFile(UploadReq req);

    String uploadForCaptcha(UploadForCaptchaImageReq req);

    Map<String, String> getThumbnail(List<String> fileUuids);
}
