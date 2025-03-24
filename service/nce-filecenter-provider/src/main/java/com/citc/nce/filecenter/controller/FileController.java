package com.citc.nce.filecenter.controller;

import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.platform.vo.UpReceiveData;
import com.citc.nce.filecenter.service.FileManageService;
import com.citc.nce.filecenter.service.IAvailableQuantityService;
import com.citc.nce.filecenter.util.DurationUtil;
import com.citc.nce.filecenter.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/24 11:43
 * @Version: 1.0
 * @Description:
 */
@RestController
@Slf4j
public class FileController implements FileApi {

    @Resource
    private FileManageService fileService;
    @Resource
    private IAvailableQuantityService availableQuantityService;


    @Override
    @PostMapping(value = "/file/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<UploadResp> uploadFile(UploadReq uploadReq) {
        return setFileLength(fileService.uploadFile(uploadReq, "robot"));
    }

    @Override
    @PostMapping(value = "/file/simpleUpload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<UploadResp> simpleUpload(UploadReq uploadReq) {
        return setFileLength(fileService.simpleUploadFile(uploadReq, "robot"));
    }

    @Override
    @PostMapping(value = "/file/download")
    public ResponseEntity<byte[]> download(@RequestBody @Valid DownloadReq req) {
        return fileService.downloadFile(req.getFileUUID());
    }

    @Override
    @PostMapping(value = "/file/delete")
    public void deleteFile(@RequestBody @Valid DeleteReq req) {
        fileService.deleteFile(req);
    }

    @Override
    @PostMapping("/file/account/details")
    public List<AccountDetails> getDetails() {
        return availableQuantityService.getDetails();
    }

    @Override
    @PostMapping("/file/examine")
    public List<UploadResp> examine(@RequestBody @Valid ExamineReq examineReq) {
        return setFileLength(fileService.examine(examineReq));
    }

    @Override
    public UploadResp examineOne(ExamineOneReq examineOneReq) {
        return fileService.examineOne(examineOneReq);
    }

    @GetMapping(value = "/file/download/id")
    public ResponseEntity<byte[]> download2(@RequestParam String req) {
        return fileService.downloadFile(req);
    }

    @PostMapping(value = "/file/upToConsumer", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Override
    public UpReceiveData upToConsumer(UploadReq uploadReq) {
        return fileService.upToConsumer(uploadReq);
    }

    @Override
    @PostMapping(value = "/file/getFileInfo")
    public FileInfo getFileInfo(@RequestBody @Valid FileInfoReq req) {
        return fileService.getFileInfo(req.getUuid());
    }

    @Override
    @PostMapping(value = "/file/videoDownload")
    public ResponseEntity<byte[]> videoDownload(@RequestBody @Valid DownloadReq downloadReq) {
        return fileService.videoDownload(downloadReq);
    }

    @Override
    @PostMapping(value = "/file/getFileInfoByScenarioId")
    public FileInfo getFileInfoByScenarioId(@RequestBody @Valid FileInfoReq fileInfoReq) {
        return fileService.getFileInfoByScenarioId(fileInfoReq);
    }

    @Override
    @PostMapping(value = "/file/uploadForCaptcha")
    public String uploadForCaptcha(UploadForCaptchaImageReq req) {
        return fileService.uploadForCaptcha(req);
    }

    @Override
    @PostMapping(value = "/file/getAutoThumbnail")
    public Map<String, String> getAutoThumbnail(@RequestParam("fileUuids") List<String> fileUuids) {
        return fileService.getThumbnail(fileUuids);
    }

    @GetMapping(value = "/downloadFile")
    @Override
    public ResponseEntity<byte[]> download3(@RequestParam("url") String url) {
        System.out.println("原url : " + url);
        if (StringUtils.contains(url, "isoftstone")) {
            url = url.replaceAll("isoftstone/", "");
            System.out.println("处理后url : " + url);
        }
        url = url.substring(0, url.lastIndexOf("."));
        return fileService.downloadFile(url);
    }


    /**
     * 转换文件大小
     *
     * @param list 需要转换的list
     * @return 入参数
     */
    private List<UploadResp> setFileLength(List<UploadResp> list) {
        if (CollectionUtils.isEmpty(list)) return list;
        for (UploadResp s : list) {
            if (Objects.isNull(s.getFileLength()) && StringUtils.isNotBlank(s.getFileSize())) {
                s.setFileLength(DurationUtil.stringToLong(s.getFileSize()));
            }
        }
        return list;
    }
}

