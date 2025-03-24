package com.citc.nce.auth.contactlist.controller;



import com.citc.nce.auth.contactlist.ContactListApi;
import com.citc.nce.auth.contactlist.service.ContactListService;
import com.citc.nce.auth.contactlist.vo.*;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 联系人组联系人管理
 */
@RestController()
@Slf4j
public class ContactListController implements ContactListApi {
    @Resource
    private ContactListService contactListService;


    /**
     * 联系人组联系人管理列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "联系人组联系人管理列表分页获取", notes = "联系人组联系人管理列表分页获取")
    @PostMapping("/contact/list/pageList")
    @Override
    public PageResultResp getContactLists(@RequestBody  @Valid ContactListPageReq contactListPageReq) {
        return contactListService.getContactLists(contactListPageReq);
    }



    /**
     * 新增联系人组联系人管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "新增联系人组联系人管理", notes = "新增联系人组联系人管理")
    @PostMapping("/contact/list/save")
    @Override
    public int saveContactList(@RequestBody  @Valid ContactListReq ContactListReq) {
        return contactListService.saveContactList(ContactListReq);
    }

    /**
     * 修改联系人组联系人管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "修改联系人组联系人管理", notes = "修改联系人组联系人管理")
    @PostMapping("/contact/list/edit")
    @Override
    public int updateContactList(@RequestBody  @Valid ContactListEditReq ContactListEditReq) {
        return contactListService.updateContactList(ContactListEditReq);
    }

    /**
     * 删除联系人组联系人管理
     *
     * @param
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long",required=true),
            @ApiImplicitParam(name = "groupId", value = "groupId", dataType = "Long",required=true)
    })
    @ApiOperation(value = "删除联系人组联系人管理", notes = "删除联系人组联系人管理")
    @PostMapping("/contact/list/delete")
    @Override
    public int delContactListById(@RequestParam("id") Long id,@RequestParam("groupId") Long groupId) {
        return contactListService.delContactListById(id,groupId);
    }

    /**
     * 导出联系人组联系人模板
     *
     * @param
     * @return
     */
    @ApiOperation(value = "导出联系人模板", notes = "导出联系人模板")
    @PostMapping("/contact/list/exportContactListTemple")
    @Override
    public ResponseEntity<byte[]> exportContactListTemple() {
        return contactListService.exportContactListTemple();
    }

    @ApiOperation(value = "导入联系人", notes = "导入联系人")
    @PostMapping("/contact/list/importContactList")
    @Override
    public ImportContactListResp importContactListTemple(@RequestParam(value = "file") MultipartFile file,  @RequestParam("groupId") Long groupId) {
        return contactListService.importContactListTemple(file,groupId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "groupId", dataType = "Long",required=true)
    })
    @ApiOperation(value = "获取当前组联系人最后修改时间", notes = "获取当前组联系人最后修改时间")
    @PostMapping("/contact/list/getContactListUpdateTimeByGroupId")
    @Override
    public Map<String,Object> getContactListUpdateTimeByGroupId(@RequestParam("groupId") Long groupId) {
        return contactListService.getContactListUpdateTimeByGroupId(groupId);
    }

    @ApiOperation(value = "根据手机号删除联系人组联系人管理", notes = "根据手机号删除联系人组联系人管理")
    @PostMapping("/contact/list/delContactListByPhone")
    public int delContactListByPhone(@RequestBody  @Valid ContactListReq contactListReq) {
        return  contactListService.delContactListByPhone(contactListReq);
    }
    /**
     * 联系人组联系人列表获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "获取联系人组联系人管理", notes = "获取联系人组联系人管理")
    @PostMapping("/contact/list/list")
    @Override
    public List<ContactListResp> getContactListAlls(@RequestBody  @Valid ContactListPageReq contactListPageReq) {
        return contactListService.getContactListAlls(contactListPageReq);
    }

    @Override
    public Map<Long, List<ContactListResp>> getContactListByGroupIds(List<Long> groupIds) {
        return contactListService.getContactListByGroupIds(groupIds);
    }
    /**
     * 获取单个联系人组联系人管理
     *
     * @param
     * @return
     */
  /*  @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long",required=true)
    })
    @ApiOperation(value = "获取单个联系人组联系人管理", notes = "获取单个联系人组联系人管理")
    @PostMapping("/contact/list/getOne")
    public ContactListResp getContactListById(@RequestParam("id") Long id) {
        return contactListService.getContactListById(id);
    }*/
}
