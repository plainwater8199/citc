package com.citc.nce.auth.contactgroup.service;

import com.citc.nce.auth.contactgroup.vo.*;
import com.citc.nce.auth.messagetemplate.vo.PageResultResp;
import com.citc.nce.common.core.pojo.PageParam;

import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/11 15:27
 * @Version: 1.0
 * @Description:
 */
public interface ContactGroupContService {
    PageResultResp getContactGroups(PageParam pageParam);

    int saveContactGroup(ContactGroupReq contactGroupReq);

    int updateContactGroup(ContactGroupEditReq contactGroupEditReq);

    int delContactGroupById(Long id);

    List<ContactGroupTreeResp> getTreeList();

    ContactGroupResp getContactGroupById(Long id);

    ContactGroupSelectedCarriersResp getContactGroupMemberById(ContactGroupSelectedCarriersReq req);

    Integer importContactForModule(List<String> phoneList, Long groupId, String groupName);
}
