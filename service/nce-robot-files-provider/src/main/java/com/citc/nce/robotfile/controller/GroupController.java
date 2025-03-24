package com.citc.nce.robotfile.controller;


import com.citc.nce.dto.GroupNameReq;
import com.citc.nce.dto.GroupReq;
import com.citc.nce.dto.PageReq;
import com.citc.nce.fileApi.GroupApi;
import com.citc.nce.robotfile.service.IGroupService;
import com.citc.nce.robotfile.service.IPictureService;
import com.citc.nce.vo.GroupResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: GroupController
 */
@Api(value = "分组管理")
@RestController
public class GroupController implements GroupApi {
    
    @Resource
    private IGroupService groupService;

    @Resource
    private IPictureService pictureService;

    @Override
    @ApiOperation(value = "保存一个分组")
    @PostMapping(value = "/material/group/save")
    public void saveGroup(@RequestBody @Valid GroupNameReq req){
        groupService.saveGroup(req.getGroupName());
    }

    @Override
    @ApiOperation(value = "展示所有分组")
    @PostMapping(value = "/material/group/list")
    public List<GroupResp> selectAllGroups(){
        return groupService.selectAll();
    }

    @Override
    @ApiOperation(value = "分组管理展示")
    @PostMapping(value = "/material/group/manage")
    public List<GroupResp> manageGroup(@RequestBody PageReq pageReq){
        return pictureService.manage(pageReq);
    }

    @Override
    @ApiOperation(value = "对分组进行管理")
    @PostMapping(value = "/material/group/update")
    public void updateGroup(@RequestBody @Valid List<GroupReq> vos){
        groupService.updateGroup(vos);
    }

}
