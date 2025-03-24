package com.citc.nce.customcommand;

import com.citc.nce.annotation.BossAuth;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.customcommand.vo.*;
import com.citc.nce.customcommand.vo.resp.SearchListForMSResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author jiancheng
 */
@BossAuth({"/chatbot-view/directive/produce","/chatbot-view/shop/storeroom-add","/chatbot-view/shop/storeroom-edit"})
@RestController
@Api(value = "customCommandController", tags = "自定义指令生产")
@RequiredArgsConstructor
@RequestMapping("customCommand")
public class CustomCommandController {
    private final CustomCommandApi customCommandApi;

    @PostMapping("add")
    @ApiOperation("新增指令")
    public void add(@RequestBody @Valid CustomCommandAddReq addReq) {
        customCommandApi.add(addReq);
    }

    @PostMapping("search")
    @ApiOperation("不按状态搜索指令")
    public PageResult<CustomCommandSimpleVo> searchCommand(@RequestBody @Valid CustomCommandSearchReq searchReq) {
        return customCommandApi.searchCommand(searchReq);
    }

    @PostMapping("publish/search")
    @ApiOperation("搜索已发布指令")
    public PageResult<CustomCommandSimpleVo> searchPublishCommand(@RequestBody @Valid CustomCommandSearchReq searchReq) {
        return customCommandApi.searchPublishCommand(searchReq);
    }

    @ApiOperation("作品制作--组件搜索")
    @PostMapping("/ms/searchList")
    SearchListForMSResp searchListForMS(@RequestBody SearchListForMSReq req){
        return customCommandApi.searchListForMS(req);
    }

    @PostMapping("{id}/publish")
    @ApiOperation("发布指令")
    public void publish(@PathVariable("id") Long id) {
        customCommandApi.publish(id);
    }

    @PostMapping("edit")
    @ApiOperation("编辑指令")
    public void edit(@RequestBody @Valid CustomCommandEditReq editReq) {
        customCommandApi.edit(editReq);
    }

    @PostMapping("{id}/delete")
    @ApiOperation("删除指令")
    public void delete(@PathVariable("id") Long id) {
        customCommandApi.delete(id);
    }

    @PostMapping("{id}/restore")
    @ApiOperation("还原指令")
    public void restore(@PathVariable("id") Long id) {
        customCommandApi.restore(id);
    }

    @PostMapping("active")
    @ApiOperation("修改指令可用状态")
    public void active(@RequestBody @Valid CustomCommandActiveReq activeReq) {
        customCommandApi.active(activeReq);
    }

    @PostMapping("{id}/detail")
    @ApiOperation("查询指令详情")
    public CustomCommandDetailVo getDetail(@PathVariable("id") Long id) {
        return customCommandApi.getDetail(id);
    }

    @PostMapping("my/available")
    @ApiOperation("查询用户可用指令")
    public PageResult<MyAvailableCustomCommandVo> getMyAvailableCommand(@RequestBody @Valid MyAvailableCustomCommandReq req) {
        return customCommandApi.getMyAvailableCommand(req);
    }

}
