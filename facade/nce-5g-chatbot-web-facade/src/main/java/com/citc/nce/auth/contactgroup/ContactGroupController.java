package com.citc.nce.auth.contactgroup;


import com.citc.nce.auth.contactgroup.vo.*;
import com.citc.nce.auth.messagetemplate.vo.PageResultResp;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.facadeserver.annotations.Examine;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 联系人组管理
 */

@RestController
@Slf4j
@Api(value = "auth", tags = "联系人组管理")
public class ContactGroupController {
    @Resource
    private ContactGroupApi contactGroupApi;


    /**
     * 联系人组管理列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "联系人组管理列表分页获取", notes = "联系人组管理列表分页获取")
    @PostMapping("/contact/group/pageList")
    public PageResultResp getContactGroups(@RequestBody  @Valid PageParam pageParam) {
        return contactGroupApi.getContactGroups(pageParam);
    }

    /**
     * 新增联系人组管理
     *
     * @param
     * @return
     */
    @Examine
    @ApiOperation(value = "新增联系人组管理", notes = "新增联系人组管理")
    @PostMapping("/contact/group/save")
    public int saveContactGroup(@RequestBody  @Valid ContactGroupReq contactGroupReq) {
        return contactGroupApi.saveContactGroup(contactGroupReq);
    }

    /**
     * 修改联系人组管理
     *
     * @param
     * @return
     */
    @Examine
    @ApiOperation(value = "修改联系人组管理", notes = "修改联系人组管理")
    @PostMapping("/contact/group/edit")
    public int updateContactGroup(@RequestBody  @Valid ContactGroupEditReq contactGroupEditReq) {
        return contactGroupApi.updateContactGroup(contactGroupEditReq);
    }

    /**
     * 删除联系人组管理
     *
     * @param
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long",required=true)
    })
    @ApiOperation(value = "删除联系人组管理", notes = "删除联系人组管理")
    @PostMapping("/contact/group/delete")
    public int delContactGroupById(@RequestParam("id") Long id) {
        return contactGroupApi.delContactGroupById(id);
    }

    /**
     * 获取单个联系人组管理
     *
     * @param
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long",required=true)
    })
    @ApiOperation(value = "获取单个联系人组管理", notes = "获取单个联系人组管理")
    @PostMapping("/contact/group/getOne")
    public ContactGroupResp getContactGroupById(@RequestParam("id") Long id) {
        return contactGroupApi.getContactGroupById(id);
    }

    /**
     * 获取联系人组树
     *
     * @param
     * @return
     */
    @ApiOperation(value = "获取联系人组树", notes = "获取联系人组树")
    @GetMapping("/contact/group/getTreeList")
    public List<ContactGroupTreeResp> getTreeList() {
        return contactGroupApi.getTreeList();
    }

    @ApiOperation(value = "获取联系人中属于某些运营商的数量", notes = "获取联系人中属于某些运营商的数量")
    @PostMapping("/contact/group/getNumOfSelectedCarriers")
    public ContactGroupSelectedCarriersResp getNumOfSelectedCarriers(@RequestBody ContactGroupSelectedCarriersReq req) {
        return contactGroupApi.getNumOfSelectedCarriers(req);
    }

}
