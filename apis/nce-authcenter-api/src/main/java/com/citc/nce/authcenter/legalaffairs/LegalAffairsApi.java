package com.citc.nce.authcenter.legalaffairs;

import com.citc.nce.authcenter.legalaffairs.vo.req.IdReq;
import com.citc.nce.authcenter.legalaffairs.vo.req.PageReq;
import com.citc.nce.authcenter.legalaffairs.vo.req.UpdateReq;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalFileNewestResp;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalFileResp;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalRecordResp;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Api(tags = "用户--法务模块")
@FeignClient(value = "authcenter-service", contextId = "legal-affairs", url = "${authCenter:}")
public interface LegalAffairsApi {

    @ApiOperation("分页查询法务文件列表")
    @PostMapping("/legal/listByPage")
    PageResult<LegalFileResp> listByPage(PageReq req);

    @ApiOperation("查询法务文件详情")
    @PostMapping("/legal/findById")
    LegalFileResp findById(IdReq req);

    @ApiOperation("修改法务文件")
    @PostMapping("/legal/updateById")
    void updateById(UpdateReq req);

    @ApiOperation("分页查询法务文件历史版本列表")
    @PostMapping("/legal/historyVersionList")
    PageResult<LegalFileResp> historyVersionList(PageReq req);

    /**
     * 获取最新法务文件
     * @return
     */
    @PostMapping("/legal/newestList")
    List<LegalFileNewestResp> newestList();

    /**
     * 获取文件操作记录
     * @return
     */
    @ApiOperation("获取文件操作记录")
    @PostMapping("/legal/findRecord")
    List<LegalRecordResp> findRecord(IdReq req);
}
