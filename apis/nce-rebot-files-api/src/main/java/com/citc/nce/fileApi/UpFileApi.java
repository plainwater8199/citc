package com.citc.nce.fileApi;

import com.citc.nce.dto.IdReq;
import com.citc.nce.dto.PageReq;
import com.citc.nce.dto.UpFileReq;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.PageResultResp;
import com.citc.nce.vo.UpFileResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日17:50:49
 * @Version: 1.0
 * @Description: UpFileDto
 */
@FeignClient(contextId = "upfile",value = "robot-files-service", url = "${robotFile:}")
public interface UpFileApi {

    @PostMapping(value = "/material/file/save")
    void saveUpFile(@RequestBody UpFileReq upFileDto);

    @PostMapping(value = "/material/file/list")
    PageResultResp<UpFileResp> selectAll(@RequestBody PageReq req);

    @PostMapping(value = "/material/file/delete")
    DeleteResp deleteUpFile(@RequestBody IdReq req);
}
