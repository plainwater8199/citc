package com.citc.nce.auth.contactbacklist;

import cn.hutool.core.collection.CollectionUtil;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListEditReq;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListPageReq;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListReq;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListResp;
import com.citc.nce.auth.contactlist.vo.ImportContactListResp;
import com.citc.nce.auth.messagetemplate.vo.PageResultResp;
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

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 黑名单联系人
 */

@RestController
@Slf4j
@Api(value = "auth", tags = "黑名单联系人管理")
public class ContactBackListController {
    @Resource
    private ContactBackListApi contactBackListApi;
    @Resource
    private ECDHService ecdhService;

    /**
     * 黑名单联系人列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "黑名单联系人列表分页获取", notes = "黑名单联系人列表分页获取")
    @PostMapping("/contact/backlist/pageList")
    public PageResultResp<ContactBackListResp> getContactBackLists(@RequestBody @Valid ContactBackListPageReq contactBackListPageReq) {
        PageResultResp<ContactBackListResp> page = contactBackListApi.getContactBackLists(contactBackListPageReq);
        if (CollectionUtil.isNotEmpty(page.getList())) {
            for (ContactBackListResp body : page.getList()) {
                body.setPhoneNum(ecdhService.encode(body.getPhoneNum()));
            }
        }
        return page;
    }

    /**
     * 新增黑名单联系人
     *
     * @param
     * @return
     */
    @Examine
    @ApiOperation(value = "新增黑名单联系人", notes = "新增黑名单联系人")
    @PostMapping("/contact/backlist/save")
    public int saveContactBackList(@RequestBody @Valid ContactBackListReq contactBackListReq) {
        return contactBackListApi.saveContactBackList(contactBackListReq);
    }

    /**
     * 修改黑名单联系人
     *
     * @param
     * @return
     */
    @ApiOperation(value = "修改黑名单联系人", notes = "修改黑名单联系人")
    @PostMapping("/contact/backlist/edit")
    public int updateContactBackList(@RequestBody @Valid ContactBackListEditReq contactBackListEditReq) {
        return contactBackListApi.updateContactBackList(contactBackListEditReq);
    }

    /**
     * 删除黑名单联系人
     *
     * @param
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", required = true)
    })
    @ApiOperation(value = "删除黑名单联系人", notes = "删除黑名单联系人")
    @PostMapping("/contact/backlist/delete")
    public int delContactBackListById(@RequestParam("id") Long id) {
        return contactBackListApi.delContactBackListById(id);
    }

    /**
     * 导出黑名单联系人模板
     *
     * @param
     * @return
     */
    @ApiOperation(value = "导出黑名单联系人模板", notes = "导出黑名单联系人模板")
    @GetMapping("/contact/backlist/exportContactBackListTemple")
    public ResponseEntity<byte[]> exportContactBackListTemple() {
        return contactBackListApi.exportContactBackListTemple();
    }

    /**
     * 导入黑名单联系人
     *
     * @param
     * @return
     */
    @ApiOperation(value = "导入黑名单联系人", notes = "导入黑名单联系人")
    @PostMapping("/contact/backlist/importContactBackListTemple")
    public ImportContactListResp importContactBackListTemple(@RequestParam(value = "file") MultipartFile file) {
        return contactBackListApi.importContactBackListTemple(file);
    }


}
