package com.citc.nce.file;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.DownloadReq;
import com.citc.nce.filecenter.vo.FileInfo;
import com.citc.nce.filecenter.vo.FileInfoReq;
import com.citc.nce.filecenter.vo.UploadReq;
import com.citc.nce.filecenter.vo.UploadResp;
import com.google.common.base.Strings;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @Author: lidingyi
 * @Date: 2022/8/2 9:12
 * @Version: 1.0
 * @Description:
 */
@Api(tags = "后台管理-文件服务")
@RestController
public class FileController {

    @Resource
    private FileApi fileApi;


    @PostMapping("/file/upload")
    @ApiOperation("文件上传")
    public List<UploadResp> upload(UploadReq req) {
        return fileApi.uploadFile(req);
    }

    @ApiOperation("文件下载")
    @PostMapping("/file/download")
    public ResponseEntity<byte[]> download(@RequestBody @Valid DownloadReq downloadReq) {
        return fileApi.download(downloadReq);
    }
    @ApiOperation("通过id文件下载")
    @GetMapping(value = "/file/download/id")
    public ResponseEntity<byte[]> download2(@RequestParam String req, HttpServletRequest request) {
        String range = request.getHeader("Range");
        if (!Strings.isNullOrEmpty(req)) {
            //1、获取视频流文件
            FileInfoReq fileInfoReq = new FileInfoReq();
            fileInfoReq.setUuid(req);
            FileInfo fileInfo = fileApi.getFileInfo(fileInfoReq);
            List<String> videoFormatList = Arrays.asList("mp4", "3gp", "webm");
            //视频文件处理
            if (videoFormatList.contains(fileInfo.getFileFormat().toLowerCase(Locale.ROOT)) && !Strings.isNullOrEmpty(range)){
                DownloadReq downloadReq = new DownloadReq();
                downloadReq.setFileUUID(req);
                downloadReq.setRange(range);
                return fileApi.videoDownload(downloadReq);
            }else{//其它文件处理
                DownloadReq downloadReq = new DownloadReq();
                downloadReq.setFileUUID(req);
                return fileApi.download(downloadReq);
            }
        }else{
            throw new BizException(GlobalErrorCode.BAD_REQUEST);
        }
    }
}
