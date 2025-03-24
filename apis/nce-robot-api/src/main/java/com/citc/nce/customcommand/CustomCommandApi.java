package com.citc.nce.customcommand;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.customcommand.vo.*;
import com.citc.nce.customcommand.vo.resp.SearchListForMSResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author jiancheng
 */
@FeignClient(value = "rebot-service", contextId = "CustomCommandApi", url = "${robot:}")
public interface CustomCommandApi {

    @PostMapping("/customCommand/add")
    void add(@RequestBody CustomCommandAddReq addReq);

    @PostMapping("/customCommand/search")
    PageResult<CustomCommandSimpleVo> searchCommand(@RequestBody CustomCommandSearchReq searchReq);

    @PostMapping("/customCommand/publish/search")
    PageResult<CustomCommandSimpleVo> searchPublishCommand(@RequestBody CustomCommandSearchReq searchReq);

    @PostMapping("/customCommand/{id}/publish")
    void publish(@PathVariable("id") Long id);

    @PostMapping("/customCommand/edit")
    void edit(@RequestBody CustomCommandEditReq editReq);

    @PostMapping("/customCommand/{id}/delete")
    void delete(@PathVariable("id") Long id);

    @PostMapping("/customCommand/{id}/restore")
    void restore(@PathVariable("id") Long id);

    @PostMapping("/customCommand/active")
    void active(@RequestBody CustomCommandActiveReq activeReq);

    @PostMapping("/customCommand/{id}/detail")
    CustomCommandDetailVo getDetail(@PathVariable("id") Long id);

    @PostMapping("/customCommand/my/available")
    PageResult<MyAvailableCustomCommandVo> getMyAvailableCommand(@RequestBody MyAvailableCustomCommandReq req);

    @PostMapping("/customCommand/my")
    PageResult<MyAvailableCustomCommandVo> getMyCommand(MyAvailableCustomCommandReq req);

    @PostMapping("/customCommand/getDetailByUuid/{uuid}")
    CustomCommandDetailVo getDetailByUuid(@PathVariable("uuid") String uuid);

    @PostMapping("/customCommand/useCommand")
    void useCommand(@RequestBody CustomCommandDetailVo commandDetailVo);


    @PostMapping("/customCommand/ms/searchList")
    SearchListForMSResp searchListForMS(@RequestBody SearchListForMSReq req);

    @GetMapping("/customCommand/ms/getByUuid/{uuid}")
    CustomCommandDetailVo getByUuid(@PathVariable("uuid") String uuid);

    @PostMapping("/customCommand/ms/getByUuid/{id}/{mssId}")
    void updateMssID(@PathVariable("id")String id, @PathVariable("mssId")Long mssId);

    @PostMapping("/customCommand/ms/deleteMssIDForIds")
    void deleteMssIDForIds(@RequestBody List<Long> mssIDList);

    @GetMapping("/customCommand/ms/getById/{id}")
    CustomCommandDetailVo getById(@PathVariable("id") String id);
}
