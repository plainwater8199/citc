package com.citc.nce.h5;

import com.citc.nce.auth.formmanagement.tempStore.Csp4CustomerFrom;
import com.citc.nce.auth.formmanagement.vo.FormManagementTreeResp;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.h5.vo.H5InfoListQuery;
import com.citc.nce.h5.vo.H5FormInfo;
import com.citc.nce.h5.vo.H5Info;
import com.citc.nce.h5.vo.req.H5FromSubmitReq;
import com.citc.nce.h5.vo.req.H5QueryVO;
import com.citc.nce.h5.vo.resp.H5CopyResp;
import com.citc.nce.h5.vo.resp.H5Resp;
import com.citc.nce.robot.api.materialSquare.vo.summary.H5TemplateInfo;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.H5TemplateListQueryReq;
import com.citc.nce.robot.api.tempStore.bean.form.FormInfoPageQuery;
import com.citc.nce.robot.api.tempStore.bean.form.FormPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesForm;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@FeignClient(value = "rebot-service",contextId="H5Api", url = "${robot:}")
public interface H5Api {
    @GetMapping("/h5/one")
    H5Info previewData(@RequestParam("id") Long id);

    @ApiOperation("h5 详情")
    @GetMapping("/h5/detail")
    H5Info getDetail(@RequestParam("id") Long id);


    @ApiOperation("h5-列表")
    @PostMapping("/h5/list")
    PageResult<H5Info> page(@RequestBody H5QueryVO h5Vo);


    @ApiOperation("h5-添加")
    @PostMapping("/h5/create")
    H5Resp create(@RequestBody @Valid H5Info h5);


    @ApiOperation("h5-更新")
    @PostMapping("/h5/update")
    void update(@RequestBody @Valid H5Info h5Temp);


    @ApiOperation("h5-删除")
    @GetMapping("/h5/delete")
    void delete(@RequestParam("id") Long id);


    @ApiOperation("h5-复制")
    @PostMapping("/h5/copy")
    H5CopyResp copy(@RequestBody H5Info h5Temp);

    @ApiOperation("h5-表单提交")
    @PostMapping("/h5/form/submit")
    void formSubmit(@RequestBody H5FromSubmitReq req);

    @ApiOperation("h5-根据活动ID获取")
    @GetMapping("/h5/form/getByActivityId")
    H5Info getByActivityId(@RequestParam("mcActivityId") Long mcActivityId);
    @ApiOperation("h5-获取H5模板列表")
    @PostMapping("/h5/form/template/list")
    List<H5TemplateInfo> h5TemplateListQuery(@RequestBody H5TemplateListQueryReq req);

    @ApiOperation("h5-CSP查询H5模版列表")
    @PostMapping("/h5/form/template/pageListForCSP")
    PageResult<ResourcesForm> pageListForCSP(@RequestBody FormPageQuery query);

    @ApiOperation("h5-CSP客户查询H5模版列表")
    @GetMapping("/h5/form/template/getTreeListForCustomer")
    List<FormManagementTreeResp> getTreeListForCustomer();

    @ApiOperation("h5-更新H5模版的mssID")
    @GetMapping("/h5/form/template/updateMssID")
    void updateMssID(@RequestParam("msId") String msId, @RequestParam("mssId") Long mssId);

    @ApiOperation("h5-用户购买H5模版后保存到用户的H5列表中")
    @GetMapping("/h5/form/template/saveForCustomer")
    void saveForCustomer(@RequestParam("msId") Long msId, @RequestParam("userId")String userId, @RequestParam("mssId") Long mssId);

    @ApiOperation("h5-根据ID删除表单内容")
    @PostMapping("/h5/form/deleteInfo")
    void formDeleteInfo(@RequestParam("id") Long id);

    @ApiOperation("h5-获取表单列表")
    @PostMapping("/h5/form/queryList")
    PageResult<H5FormInfo> formQueryList(@RequestBody FormInfoPageQuery query);

    @ApiOperation("h5-删除和作品之间的关系")
    @PostMapping("/h5/form/deleteMssIDForIds")
    void deleteMssIDForIds(@RequestParam("h5OfSummeryIdList") List<Long> h5OfSummeryIdList);

    @ApiOperation("h5-根据ID查询所有信息")
    @PostMapping("/h5/form/list/byIds")
    List<H5Info> byIds(@RequestBody H5InfoListQuery query);

    @ApiOperation("h5-对于作品的H5生成备份")
    @PostMapping("/h5/createSummeryH5")
    Long createSummeryH5(@RequestParam("msId") String msId);

    @ApiOperation("h5-对于作品的H5获取备份的数据")
    @PostMapping("/h5/getDetailForSummery")
    H5Info getDetailForSummery(@RequestParam("msId") Long msId);

    @ApiOperation("h5-对于作品的H5g更新备份")
    @PostMapping("/h5/updateSummaryH5")
    void updateSummaryH5(@RequestParam("msId") String msId);
    @ApiOperation("h5-导入用户的相模型")
    @GetMapping("/h5/form/template/importH5ForCustomer")
    void importH5ForCustomer(@RequestParam("mssId") Long mssId, @RequestParam("userId")String userId, @RequestParam("name") String name);


    @ApiOperation("h5-机器人和5G消息导入表单")
    @GetMapping("/h5/form/template/importH5ForRobotAndMSG")
    Map<Long, Csp4CustomerFrom> importH5ForRobotAndMSG(@RequestParam("h5Ids")List<Long> h5Ids);
}

