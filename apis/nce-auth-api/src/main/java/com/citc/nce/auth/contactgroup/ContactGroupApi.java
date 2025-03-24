package com.citc.nce.auth.contactgroup;

import com.citc.nce.auth.contactgroup.vo.*;
import com.citc.nce.auth.messagetemplate.vo.PageResultResp;
import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/10 17:19
 * @Version: 1.0
 * @Description: 联系人组
 */
@FeignClient(value = "auth-service", contextId = "ContactGroup", url = "${auth:}")
public interface ContactGroupApi {

    /**
     * 联系人组列表分页获取
     *
     * @param
     * @return
     */
    @PostMapping("/contact/group/pageList")
    PageResultResp getContactGroups(@RequestBody @Valid PageParam pageParam);

    /**
     * 新增联系人组
     *
     * @param
     * @return
     */
    @PostMapping("/contact/group/save")
    int saveContactGroup(@RequestBody @Valid ContactGroupReq contactGroupReq);

    /**
     * 修改联系人组
     *
     * @param
     * @return
     */
    @PostMapping("/contact/group/edit")
    int updateContactGroup(@RequestBody @Valid ContactGroupEditReq contactGroupEditReq);

    /**
     * 删除联系人组
     *
     * @param
     * @return
     */
    @PostMapping("/contact/group/delete")
    int delContactGroupById(@RequestParam("id") Long id);

    /**
     * 获取联系人组树
     *
     * @param
     * @return
     */
    @PostMapping("/contact/group/getTreeList")
    List<ContactGroupTreeResp> getTreeList();

    /**
     * 获取单个联系人组管理
     *
     * @param
     * @return
     */
    @PostMapping("/contact/group/getOne")
    ContactGroupResp getContactGroupById(@RequestParam("id") Long id);

    @ApiOperation(value = "获取联系人中属于某些运营商的数量", notes = "获取联系人中属于某些运营商的数量")
    @PostMapping("/contact/group/getNumOfSelectedCarriers")
    ContactGroupSelectedCarriersResp getNumOfSelectedCarriers(@RequestBody ContactGroupSelectedCarriersReq req);
    /**
     * 导入组件的手机
     * @param phoneList 手机号列表
     * @param groupId 联系人组ID
     * @param groupName 联系人组名
     * @return 导入成功数量
     */
    @PostMapping("/contact/group/importContactForModule")
    Integer importContactForModule(@RequestParam("phoneList")List<String> phoneList, @RequestParam(value="groupId",required = false)Long groupId, @RequestParam("groupName")String groupName);
}
