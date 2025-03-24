package com.citc.nce.auth.contactlist;


import com.citc.nce.auth.contactlist.vo.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Author: yangchuang
 * @Date: 2022/8/10 17:19
 * @Version: 1.0
 * @Description:
 * 联系人组联系人
 */
@FeignClient(value = "auth-service", contextId = "ContactList", url = "${auth:}")
public interface ContactListApi {

    /**
     * 联系人组联系人列表分页获取
     *
     * @param
     * @return
     */
    @PostMapping("/contact/list/pageList")
    PageResultResp getContactLists(@RequestBody  @Valid ContactListPageReq contactListPageReq);

    /**
     * 新增联系人组联系人
     *
     * @param
     * @return
     */
    @PostMapping("/contact/list/save")
    int saveContactList(@RequestBody @Valid ContactListReq contactListReq);

    /**
     * 修改联系人组联系人
     *
     * @param
     * @return
     */
    @PostMapping("/contact/list/edit")
    int updateContactList(@RequestBody @Valid ContactListEditReq contactListEditReq);

    /**
     * 删除联系人组联系人
     *
     * @param
     * @return
     */
    @PostMapping("/contact/list/delete")
    int delContactListById(@RequestParam("id") Long id,@RequestParam("groupId") Long groupId);

    /**
     * 导出联系人组联系人模板
     *
     * @param
     * @return
     */
    @PostMapping("/contact/list/exportContactListTemple")
    ResponseEntity<byte[]> exportContactListTemple();

    @PostMapping(path = "/contact/list/importContactList",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ImportContactListResp importContactListTemple(@RequestPart(value = "file") MultipartFile file, @RequestParam("groupId") Long groupId);


    @PostMapping("/contact/list/getContactListUpdateTimeByGroupId")
    Map<String, Object> getContactListUpdateTimeByGroupId(@RequestParam("groupId") Long groupId);

    @PostMapping("/contact/list/delContactListByPhone")
    int delContactListByPhone(ContactListReq contactListReq);

    /**
     * 联系人组联系人列表获取
     *
     * @param
     * @return
     */
    @PostMapping("/contact/list/list")
    List<ContactListResp> getContactListAlls(@RequestBody  @Valid ContactListPageReq contactListPageReq);

    /**
     *
     * @param groupIds 联系人群组Id列表
     * @return key is groupId, value is contact list of the contact group
     */
    @PostMapping("/contact/list/listByGroupIds")
    Map<Long,List<ContactListResp>> getContactListByGroupIds(@RequestBody List<Long> groupIds);

}
