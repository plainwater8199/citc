package com.citc.nce.auth.contactgroup.controller;


import com.citc.nce.auth.contactgroup.ContactGroupApi;
import com.citc.nce.auth.contactgroup.service.ContactGroupContService;
import com.citc.nce.auth.contactgroup.vo.*;
import com.citc.nce.auth.messagetemplate.vo.PageResultResp;
import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 联系人组管理
 */
@RestController()
@Slf4j
public class ContactGroupController implements ContactGroupApi {
    @Resource
    private ContactGroupContService contactGroupContService;


    /**
     * 联系人组管理列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "联系人组管理列表分页获取", notes = "联系人组管理列表分页获取")
    @PostMapping("/contact/group/pageList")
    @Override
    public PageResultResp getContactGroups(@RequestBody  @Valid PageParam pageParam) {
        return contactGroupContService.getContactGroups(pageParam);
    }

    /**
     * 新增联系人组管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "新增联系人组管理", notes = "新增联系人组管理")
    @PostMapping("/contact/group/save")
    @Override
    public int saveContactGroup(@RequestBody  @Valid ContactGroupReq contactGroupReq) {
        return contactGroupContService.saveContactGroup(contactGroupReq);
    }

    /**
     * 修改联系人组管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "修改联系人组管理", notes = "修改联系人组管理")
    @PostMapping("/contact/group/edit")
    @Override
    public int updateContactGroup(@RequestBody  @Valid ContactGroupEditReq contactGroupEditReq) {
        return contactGroupContService.updateContactGroup(contactGroupEditReq);
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
    @Override
    public int delContactGroupById(@RequestParam("id") Long id) {
        return contactGroupContService.delContactGroupById(id);
    }

    /**
     * 获取联系人组树
     *
     * @param
     * @return
     */
    @ApiOperation(value = "获取联系人组树", notes = "获取联系人组树")
    @PostMapping("/contact/group/getTreeList")
    @Override
    public List<ContactGroupTreeResp> getTreeList() {
        return contactGroupContService.getTreeList();
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
    @Override
    public ContactGroupResp getContactGroupById(@RequestParam("id") Long id) {
        return contactGroupContService.getContactGroupById(id);
    }

    @Override
    public ContactGroupSelectedCarriersResp getNumOfSelectedCarriers(ContactGroupSelectedCarriersReq req) {
        //找到联系人组下所有联系人
        return contactGroupContService.getContactGroupMemberById(req);
    }

    @Override
    @PostMapping("/contact/group/importContactForModule")
    public Integer importContactForModule(@RequestParam("phoneList")List<String> phoneList, @RequestParam(value="groupId",required = false)Long groupId, @RequestParam("groupName")String groupName) {
        return contactGroupContService.importContactForModule(phoneList,groupId,groupName);
    }


}
