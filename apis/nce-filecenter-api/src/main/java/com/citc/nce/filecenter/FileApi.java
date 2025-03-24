package com.citc.nce.filecenter;

import com.citc.nce.filecenter.platform.vo.UpReceiveData;
import com.citc.nce.filecenter.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/24 11:26
 * @Version: 1.0
 * @Description:
 */
@FeignClient(value = "filecenter-service", contextId = "fileServiceApi", url = "${filecenter:}")
public interface FileApi {


    @GetMapping(value = "/downloadFile")
    ResponseEntity<byte[]> download3(@RequestParam("url") String url);

    /**
     * 文件上传
     *
     * @param uploadReq
     * @return 文件id
     */
    @PostMapping(value = "/file/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    List<UploadResp> uploadFile(UploadReq uploadReq);

    /**
     * 文件简单上传
     *
     * @param uploadReq
     * @return 文件id
     */
    @PostMapping(value = "/file/simpleUpload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    List<UploadResp> simpleUpload(UploadReq uploadReq);

    /**
     * 文件下载
     *
     * @param downloadReq
     * @return
     */
    @PostMapping(value = "/file/download")
    ResponseEntity<byte[]> download(@RequestBody DownloadReq downloadReq);

    /**
     * 文件删除
     *
     * @param req
     * @return
     */
    @PostMapping(value = "/file/delete")
    void deleteFile(@RequestBody DeleteReq req);


    /**
     * 账户空间使用情况
     */
    @PostMapping("/file/account/details")
    List<AccountDetails> getDetails();

    /**
     * 送审接口
     */
    @PostMapping("/file/examine")
    List<UploadResp> examine(@RequestBody ExamineReq examineReq);

    @PostMapping("/file/examineOne")
    UploadResp examineOne(@RequestBody ExamineOneReq examineOneReq);

    @PostMapping(value = "/file/upToConsumer", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    UpReceiveData upToConsumer(UploadReq uploadReq);

    @PostMapping(value = "/file/getFileInfo")
    FileInfo getFileInfo(@RequestBody FileInfoReq Req);

    @PostMapping(value = "/file/videoDownload")
    ResponseEntity<byte[]> videoDownload(DownloadReq downloadReq);

    @PostMapping(value = "/file/getFileInfoByScenarioId")
    FileInfo getFileInfoByScenarioId(@RequestBody FileInfoReq fileInfoReq);

    @PostMapping(value = "/file/uploadForCaptcha",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    String uploadForCaptcha(UploadForCaptchaImageReq req);

    @PostMapping(value = "/file/getAutoThumbnail")
    Map<String, String> getAutoThumbnail(@RequestParam("fileUuids") List<String> fileUuids);
}
