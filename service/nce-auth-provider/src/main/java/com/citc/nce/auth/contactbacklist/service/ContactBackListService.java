package com.citc.nce.auth.contactbacklist.service;

import com.citc.nce.auth.contactbacklist.vo.ContactBackListEditReq;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListPageReq;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListReq;
import com.citc.nce.auth.contactbacklist.vo.ContactBackListResp;
import com.citc.nce.auth.contactlist.vo.ImportContactListResp;
import com.citc.nce.auth.messagetemplate.vo.PageResultResp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 16:30
 * @Version: 1.0
 * @Description:
 */
public interface ContactBackListService {

    PageResultResp<ContactBackListResp> getContactBackLists(ContactBackListPageReq contactBackListPageReq);

    int saveContactBackList(ContactBackListReq contactBackListReq);

    int updateContactBackList(ContactBackListEditReq contactBackListEditReq);

    int delContactBackListById(Long id);

    ResponseEntity<byte[]> exportContactBackListTemple();

    ImportContactListResp importContactBackListTemple(MultipartFile file);

    List<ContactBackListResp> getAllList(ContactBackListPageReq contactListBackPageReq);

    boolean getContactInBlack(String create, String phone);
}
