package com.citc.nce.materialSquare.h5;


import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.h5.H5CommonApi;
import com.citc.nce.materialSquare.MsManageActivityContentApi;
import com.citc.nce.materialSquare.MsManageActivityLiApi;
import com.citc.nce.materialSquare.vo.activity.*;
import com.citc.nce.materialSquare.vo.activity.resp.MsManageActivityContentResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/h5" )
@Api(tags = "H5-活动资源查询")
public class H5ActivityController {

    private final MsManageActivityContentApi msManageActivityContentApi;

    private final MsManageActivityLiApi msManageActivityLiApi;


    @ApiOperation("分页查询活动方案")
    @PostMapping("/activityContent/List")
    List<MsManageActivityContentResp> list() {
        return msManageActivityContentApi.list();
    }

    @PostMapping("/activityLi/List")
    @ApiOperation("分页查询")
    List<LiDetailResult> listByLiId(@RequestBody MssForLiReq req) {
        return msManageActivityLiApi.listByLiId(req);
    }

}
