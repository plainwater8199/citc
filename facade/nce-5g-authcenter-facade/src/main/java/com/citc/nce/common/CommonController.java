package com.citc.nce.common;

import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.DownloadReq;
import com.citc.nce.filecenter.vo.UploadReq;
import com.citc.nce.filecenter.vo.UploadResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "公共管理--文件")
@RestController
public class CommonController {

    @Resource
    private FileApi fileApi;

    @PostMapping("/file/upload")
    @ApiOperation("文件上传")
    public List<UploadResp> upload(UploadReq req) {
        return fileApi.uploadFile(req);
    }

    @ApiOperation("文件下载")
    @PostMapping("/")
    public ResponseEntity<byte[]> download(@RequestBody @Valid DownloadReq downloadReq) {
        return fileApi.download(downloadReq);
    }
    @ApiOperation("通过id下载文件")
    @GetMapping(value = "/file/download/id")
    public ResponseEntity<byte[]> download2(@RequestParam String req) {
        DownloadReq downloadReq = new DownloadReq();
        downloadReq.setFileUUID(req);
        return fileApi.download(downloadReq);
    }
}
