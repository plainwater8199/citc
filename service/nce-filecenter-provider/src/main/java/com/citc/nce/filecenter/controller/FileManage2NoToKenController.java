package com.citc.nce.filecenter.controller;

import com.citc.nce.filecenter.FileManage2NoTokenApi;
import com.citc.nce.filecenter.service.FileManage2NoTokenService;
import com.citc.nce.filecenter.vo.UploadReq;
import com.citc.nce.filecenter.vo.UploadResp;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "免登录文件管理")
@RestController
@Slf4j
public class FileManage2NoToKenController implements FileManage2NoTokenApi {

    @Resource
    private FileManage2NoTokenService fileManage2NoTokenService;

    /**
     * 文件下载
     */
    @GetMapping(value = "/download2Scene")
    public ResponseEntity<byte[]> downloadScene(@RequestParam("uuid") String uuid,@RequestParam("scene") String scene){
        return fileManage2NoTokenService.downloadScene(uuid,scene);
    }

    /**
     * 文件上传
     */
    @PostMapping(value = "/uploadFile2Scene", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<UploadResp> uploadFile2Scene(UploadReq uploadReq){
        return fileManage2NoTokenService.uploadFile2Scene(uploadReq);
    }

}
