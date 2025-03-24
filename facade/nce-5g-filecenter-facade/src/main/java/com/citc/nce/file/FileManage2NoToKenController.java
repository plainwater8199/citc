package com.citc.nce.file;

import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.filecenter.FileManage2NoTokenApi;
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
public class FileManage2NoToKenController {

    @Resource
    private FileManage2NoTokenApi fileManage2NoTokenApi;

    /**
     * 文件下载
     */
    @GetMapping(value = "/download2Scene")
    @SkipToken
    public ResponseEntity<byte[]> downloadScene(@RequestParam("uuid") String uuid,@RequestParam("scene") String scene){
        return fileManage2NoTokenApi.downloadScene(uuid,scene);
    }

    /**
     * 文件上传
     */
    @SkipToken
    @PostMapping(value = "/uploadFile2Scene", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<UploadResp> uploadFile2Scene(UploadReq uploadReq){
        return fileManage2NoTokenApi.uploadFile2Scene(uploadReq);
    }

}
