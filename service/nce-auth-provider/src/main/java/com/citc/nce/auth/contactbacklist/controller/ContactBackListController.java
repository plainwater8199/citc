package com.citc.nce.auth.contactbacklist.controller;


import com.citc.nce.auth.contactbacklist.ContactBackListApi;
import com.citc.nce.auth.contactbacklist.service.ContactBackListService;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListEditReq;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListPageReq;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListReq;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListResp;
import com.citc.nce.auth.contactlist.vo.ImportContactListResp;
import com.citc.nce.auth.messagetemplate.vo.PageResultResp;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 黑名单联系人
 */
@RestController()
@Slf4j
public class ContactBackListController implements ContactBackListApi {
    @Resource
    private ContactBackListService contactBackListService;

    /**
     * 黑名单联系人列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "黑名单联系人列表分页获取", notes = "黑名单联系人列表分页获取")
    @PostMapping("/contact/backlist/pageList")
    @Override
    public PageResultResp<ContactBackListResp> getContactBackLists(@RequestBody @Valid ContactBackListPageReq contactBackListPageReq) {
        return contactBackListService.getContactBackLists(contactBackListPageReq);
    }

    /**
     * 新增黑名单联系人
     *
     * @param
     * @return
     */
    @ApiOperation(value = "新增黑名单联系人", notes = "新增黑名单联系人")
    @PostMapping("/contact/backlist/save")
    @Override
    public int saveContactBackList(@RequestBody @Valid ContactBackListReq contactBackListReq) {
        return contactBackListService.saveContactBackList(contactBackListReq);
    }

    /**
     * 修改黑名单联系人
     *
     * @param
     * @return
     */
    @ApiOperation(value = "修改黑名单联系人", notes = "修改黑名单联系人")
    @PostMapping("/contact/backlist/edit")
    @Override
    public int updateContactBackList(@RequestBody @Valid ContactBackListEditReq contactBackListEditReq) {
        return contactBackListService.updateContactBackList(contactBackListEditReq);
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
    @Override
    public int delContactBackListById(@RequestParam("id") Long id) {
        return contactBackListService.delContactBackListById(id);
    }

    /**
     * 导出黑名单联系人模板
     *
     * @param
     * @return
     */
    @ApiOperation(value = "导出黑名单联系人模板", notes = "导出黑名单联系人模板")
    @PostMapping("/contact/backlist/exportContactBackListTemple")
    @Override
    public ResponseEntity<byte[]> exportContactBackListTemple() {
        return contactBackListService.exportContactBackListTemple();
    }

    /**
     * 导入黑名单联系人
     *
     * @param
     * @return
     */
    @ApiOperation(value = "导入黑名单联系人", notes = "导入黑名单联系人")
    @PostMapping("/contact/backlist/importContactBackListTemple")
    @Override
    public ImportContactListResp importContactBackListTemple(@RequestParam(value = "file") MultipartFile file) {
        return contactBackListService.importContactBackListTemple(file);
    }

    /**
     * 黑名单联系人列表获取
     *
     * @param
     * @return
     */
    @PostMapping("/contact/backlist/getAllList")
    @Override
    public List<ContactBackListResp> getAllList(@RequestBody @Valid ContactBackListPageReq contactListBackPageReq) {
        return contactBackListService.getAllList(contactListBackPageReq);
    }

    @Override
    @GetMapping("/contact/backlist/inBlack")
    public boolean getContactInBlack(@RequestParam("create") String create, @RequestParam("phone") String phone) {
        return contactBackListService.getContactInBlack(create, phone);
    }
}
