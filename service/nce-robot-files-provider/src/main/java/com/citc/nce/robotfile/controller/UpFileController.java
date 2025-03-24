package com.citc.nce.robotfile.controller;


import com.citc.nce.dto.IdReq;
import com.citc.nce.dto.PageReq;
import com.citc.nce.dto.UpFileReq;
import com.citc.nce.fileApi.UpFileApi;
import com.citc.nce.robotfile.service.IUpFileService;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.PageResultResp;
import com.citc.nce.vo.UpFileResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;


/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: UpFileController
 */
@Api(value = "文件管理")
@RestController
public class UpFileController implements UpFileApi {

    @Resource
    private IUpFileService upFileService;

    @Override
    @ApiOperation(value = "保存文件")
    @PostMapping(value = "/material/file/save")
    public void saveUpFile(@RequestBody @Valid UpFileReq upFileDto){
        upFileService.saveUpFile(upFileDto);
    }

    @Override
    @ApiOperation(value = "分页查询文件")
    @PostMapping(value = "/material/file/list")
    public PageResultResp<UpFileResp> selectAll(@RequestBody @Valid PageReq req){
        return upFileService.selectAll(req);
    }

    @Override
    @ApiOperation(value = "删除文件")
    @PostMapping(value = "/material/file/delete")
    public DeleteResp deleteUpFile(@RequestBody @Valid IdReq req){
        return upFileService.deleteUpFile(req);
    }

}
