package com.citc.nce.auth.contactlist;

import cn.hutool.core.collection.CollectionUtil;
import com.citc.nce.auth.contactlist.vo.*;
import com.citc.nce.common.facadeserver.annotations.Examine;
import com.citc.nce.common.web.utils.dh.ECDHService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 联系人组联系人管理
 */

@RestController
@Slf4j
@Api(value = "auth", tags = "联系人组联系人管理")
public class ContactListController{
    @Resource
    private ContactListApi contactListApi;
    @Resource
    private ECDHService ecdhService;


    /**
     * 联系人组联系人管理列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "联系人组联系人管理列表分页获取", notes = "联系人组联系人管理列表分页获取")
    @PostMapping("/contact/list/pageList")
    public PageResultResp getContactLists(@RequestBody  @Valid ContactListPageReq contactListPageReq) {
        PageResultResp page = contactListApi.getContactLists(contactListPageReq);
        if (CollectionUtil.isNotEmpty(page.getList())){
            for (ContactListResp body : page.getList()) {
                body.setPhoneNum(ecdhService.encode(body.getPhoneNum()));
            }
        }
        return page;
    }

 

    /**
     * 新增联系人组联系人管理
     *
     * @param
     * @return
     */
    @Examine
    @ApiOperation(value = "新增联系人组联系人管理", notes = "新增联系人组联系人管理")
    @PostMapping("/contact/list/save")
    public int saveContactList(@RequestBody  @Valid ContactListReq ContactListReq) {
        return contactListApi.saveContactList(ContactListReq);
    }

    /**
     * 修改联系人组联系人管理
     *
     * @param
     * @return
     */
    @Examine
    @ApiOperation(value = "修改联系人组联系人管理", notes = "修改联系人组联系人管理")
    @PostMapping("/contact/list/edit")
    public int updateContactList(@RequestBody  @Valid ContactListEditReq ContactListEditReq) {
        return contactListApi.updateContactList(ContactListEditReq);
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
    public int delContactListById(@RequestParam("id") Long id,@RequestParam("groupId") Long groupId) {
        return contactListApi.delContactListById(id,groupId);
    }


    /**
     * 导出联系人组联系人模板
     *
     * @param
     * @return
     */
    @ApiOperation(value = "导出联系人模板", notes = "导出联系人模板")
    @GetMapping("/contact/list/exportContactListTemple")
    public ResponseEntity<byte[]> exportContactListTemple() {
        return contactListApi.exportContactListTemple();
    }

    /**
     * 导入联系人组联系人
     *
     * @param
     * @return
     */
    @ApiOperation(value = "导入联系人", notes = "导入联系人")
    @PostMapping("/contact/list/importContactList")
    public ImportContactListResp importContactListTemple(@RequestParam(value = "file") MultipartFile file, @RequestParam("groupId") Long groupId) {
        return contactListApi.importContactListTemple(file,groupId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "groupId", dataType = "Long",required=true)
    })
    @ApiOperation(value = "获取当前组联系人最后修改时间", notes = "获取当前组联系人最后修改时间")
    @PostMapping("/contact/list/getContactListUpdateTimeByGroupId")
    public Map<String,Object> getContactListUpdateTimeByGroupId(@RequestParam("groupId") Long groupId) {
        return contactListApi.getContactListUpdateTimeByGroupId(groupId);
    }

    /**
     * 删除联系人组联系人管理根据手机号删除
     *
     * @param
     * @return
     */
    @ApiOperation(value = "根据手机号删除联系人组联系人管理", notes = "根据手机号删除联系人组联系人管理")
    @PostMapping("/contact/list/delContactListByPhone")
    public int delContactListByPhone(@RequestBody  @Valid ContactListReq ContactListReq) {
        return contactListApi.delContactListByPhone(ContactListReq);
    }
}
