package com.citc.nce.customcommand.controller;


import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.customcommand.CustomCommandApi;
import com.citc.nce.customcommand.service.ICustomCommandService;
import com.citc.nce.customcommand.vo.*;
import com.citc.nce.customcommand.vo.resp.SearchListForMSResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 自定义指令 前端控制器
 * </p>
 *
 * @author jcrenc
 * @since 2023-11-09 02:53:48
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class CustomCommandController implements CustomCommandApi {
    private final ICustomCommandService customCommandService;

    @Override
    @PostMapping("/customCommand/add")
    public void add(@RequestBody CustomCommandAddReq addReq) {
        customCommandService.add(addReq);
    }

    @Override
    @PostMapping("/customCommand/search")
    public PageResult<CustomCommandSimpleVo> searchCommand(@RequestBody CustomCommandSearchReq searchReq) {
        return customCommandService.searchCommand(searchReq);
    }

    @Override
    @PostMapping("/customCommand/publish/search")
    public PageResult<CustomCommandSimpleVo> searchPublishCommand(@RequestBody CustomCommandSearchReq searchReq) {
        return customCommandService.searchPublishCommand(searchReq);
    }

    @PostMapping("/customCommand/{id}/publish")
    @Override
    public void publish(@PathVariable("id") Long id) {
        customCommandService.publish(id);
    }

    @PostMapping("/customCommand/edit")
    @Override
    public void edit(@RequestBody CustomCommandEditReq editReq) {
        customCommandService.edit(editReq);
    }

    @PostMapping("/customCommand/{id}/delete")
    @Override
    public void delete(@PathVariable("id") Long id) {
        customCommandService.delete(id);
    }

    @PostMapping("/customCommand/{id}/restore")
    @Override
    public void restore(@PathVariable("id") Long id) {
        customCommandService.restore(id);
    }

    @PostMapping("/customCommand/active")
    @Override
    public void active(@RequestBody CustomCommandActiveReq activeReq) {
        customCommandService.active(activeReq.getId(), activeReq.getActive());
    }

    @PostMapping("/customCommand/{id}/detail")
    @Override
    public CustomCommandDetailVo getDetail(@PathVariable("id") Long id) {
        return customCommandService.getDetail(id);
    }

    @PostMapping("/customCommand/my/available")
    @Override
    public PageResult<MyAvailableCustomCommandVo> getMyAvailableCommand(@RequestBody MyAvailableCustomCommandReq req) {
        return customCommandService.getMyAvailableCommand(req);
    }

    @PostMapping("/customCommand/my")
    @Override
    public PageResult<MyAvailableCustomCommandVo> getMyCommand(MyAvailableCustomCommandReq req) {
        return customCommandService.getMyCommand(req);
    }

    @PostMapping("/customCommand/getDetailByUuid/{uuid}")
    @Override
    public CustomCommandDetailVo getDetailByUuid(@PathVariable("uuid") String uuid) {
        return customCommandService.getDetailByUuid(uuid);
    }

    @Override
    @PostMapping("/customCommand/useCommand")
    public void useCommand(@RequestBody CustomCommandDetailVo commandDetailVo) {
        customCommandService.useCommand(commandDetailVo);
    }

    @Override
    @PostMapping("/customCommand/ms/searchList")
    public SearchListForMSResp searchListForMS(@RequestBody SearchListForMSReq req) {
        return customCommandService.searchListForMS(req);
    }

    @Override
    @GetMapping("/customCommand/ms/getByUuid/{uuid}")
    public CustomCommandDetailVo getByUuid(@PathVariable("uuid") String uuid) {
        return customCommandService.getByUuid(uuid);
    }

    @Override
    @PostMapping("/customCommand/ms/getByUuid/{id}/{mssId}")
    public void updateMssID(@PathVariable("id")String id, @PathVariable("mssId")Long mssId) {
        customCommandService.updateMssID(id, mssId);
    }

    @Override
    @PostMapping("/customCommand/ms/deleteMssIDForIds")
    public void deleteMssIDForIds(@RequestBody List<Long> mssIDList) {
        customCommandService.deleteMssIDForIds(mssIDList);
    }

    @Override
    @GetMapping("/customCommand/ms/getById/{id}")
    public CustomCommandDetailVo getById(@PathVariable("id") String id) {
        return customCommandService.getByMsId(id);
    }
}

