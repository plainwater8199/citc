package com.citc.nce.auth.contactlist.service;

import com.citc.nce.auth.contactlist.vo.*;

import com.citc.nce.common.core.pojo.PageParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 16:30
 * @Version: 1.0
 * @Description:
 */
public interface ContactListService {

    PageResultResp getContactLists(ContactListPageReq contactListPageReq);

    int saveContactList(ContactListReq contactListReq);

    int updateContactList(ContactListEditReq contactListEditReq);

    int delContactListById(Long id,Long groupId);

    ResponseEntity<byte[]> exportContactListTemple();

    ImportContactListResp importContactListTemple(MultipartFile file, Long groupId);

    Map<String, Object> getContactListUpdateTimeByGroupId(Long groupId);

    int delContactListByPhone(ContactListReq contactListReq);

    List<ContactListResp> getContactListAlls(ContactListPageReq contactListPageReq);

    Map<Long, List<ContactListResp>> getContactListByGroupIds(List<Long> groupIds);

    Integer importContact(Long id, List<String> phoneList);
}
