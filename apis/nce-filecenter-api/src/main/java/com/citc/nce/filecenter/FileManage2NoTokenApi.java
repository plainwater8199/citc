package com.citc.nce.filecenter;

import com.citc.nce.filecenter.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(value = "filecenter-service", contextId = "fileManage2NoTokenApi", url = "${filecenter:}")
public interface FileManage2NoTokenApi {


    /**
     * 文件下载
     */
    @GetMapping(value = "/download2Scene")
    ResponseEntity<byte[]> downloadScene(@RequestParam("uuid") String uuid,@RequestParam("scene") String scene);

    /**
     * 文件上传
     */
    @PostMapping(value = "/uploadFile2Scene", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    List<UploadResp> uploadFile2Scene(UploadReq uploadReq);



//    /**
//     * 文件删除
//     */
//    @PostMapping(value = "/deleteFile")
//    void deleteFile(@RequestBody DeleteReq req);

//
//    @PostMapping(value = "/file/getFileInfoByScenarioId")
//    FileInfo getFileInfoByScenarioId(@RequestBody FileInfoReq fileInfoReq);
//
//    @PostMapping(value = "/file/uploadForCaptcha",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    String uploadForCaptcha(UploadForCaptchaImageReq req);
}
