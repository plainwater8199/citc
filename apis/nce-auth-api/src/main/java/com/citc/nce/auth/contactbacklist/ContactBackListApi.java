package com.citc.nce.auth.contactbacklist;


import com.citc.nce.auth.contactbacklist.vo.ContactBackListEditReq;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListPageReq;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListReq;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListResp;
import com.citc.nce.auth.contactlist.vo.ImportContactListResp;
import com.citc.nce.auth.messagetemplate.vo.PageResultResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/10 17:19
 * @Version: 1.0
 * @Description:
 * 黑名单联系人
 */
@FeignClient(value = "auth-service", contextId = "ContactBackList", url = "${auth:}")
public interface ContactBackListApi {

    /**
     * 黑名单联系人列表分页获取
     *
     * @param
     * @return
     */
    @PostMapping("/contact/backlist/pageList")
    PageResultResp<ContactBackListResp> getContactBackLists(@RequestBody @Valid ContactBackListPageReq contactListBackPageReq);

    /**
     * 新增黑名单联系人
     *
     * @param
     * @return
     */
    @PostMapping("/contact/backlist/save")
    int saveContactBackList(@RequestBody @Valid ContactBackListReq contactBackListReq);

    /**
     * 修改黑名单联系人
     *
     * @param
     * @return
     */
    @PostMapping("/contact/backlist/edit")
    int updateContactBackList(@RequestBody @Valid ContactBackListEditReq contactBackListEditReq);

    /**
     * 删除黑名单联系人
     *
     * @param
     * @return
     */
    @PostMapping("/contact/backlist/delete")
    int delContactBackListById(@RequestParam("id") Long id);

    /**
     * 导出黑名单联系人模板
     *
     * @param
     * @return
     */
    @PostMapping("/contact/backlist/exportContactBackListTemple")
    ResponseEntity<byte[]> exportContactBackListTemple();

    @PostMapping(path = "/contact/backlist/importContactBackListTemple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ImportContactListResp importContactBackListTemple(@RequestPart(value = "file") MultipartFile file);

    /**
     * 黑名单联系人列表获取
     *
     * @param
     * @return
     */
    @PostMapping("/contact/backlist/getAllList")
    List<ContactBackListResp> getAllList(@RequestBody @Valid ContactBackListPageReq contactListBackPageReq);


    @GetMapping("/contact/backlist/inBlack")
    boolean getContactInBlack(@RequestParam("create") String create, @RequestParam("phone") String phone);
}
