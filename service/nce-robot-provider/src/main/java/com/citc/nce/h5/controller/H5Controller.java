package com.citc.nce.h5.controller;


import com.citc.nce.auth.formmanagement.tempStore.Csp4CustomerFrom;
import com.citc.nce.auth.formmanagement.vo.FormManagementTreeResp;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.h5.H5Api;
import com.citc.nce.h5.service.H5Service;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/h5")
public class H5Controller implements H5Api {

    @Autowired
    private H5Service h5Service;


    /**
     * 通过ID查询
     *
     * @param id ID
     * @return H5
     */
    @Override
    @GetMapping("/one")
    public H5Info previewData(@RequestParam("id") Long id) {
        return h5Service.previewData(id);
    }

    /**
     * h5 详情
     *
     * @param id ID
     * @return H5
     */
    @Override
    @GetMapping("/detail")
    public H5Info getDetail(@RequestParam("id") Long id) {
        return h5Service.previewDataDetail(id);
    }


    /**
     * h5-列表
     */
    @Override
    @RequestMapping("/list")
    public PageResult<H5Info> page(@RequestBody H5QueryVO h5Vo) {
        return h5Service.selectH5Page(h5Vo);
    }

    @Override
    @PostMapping("/create")
    public H5Resp create(@RequestBody H5Info h5) {
        return h5Service.create(h5);
    }

    /**
     * h5-更新
     */
    @Override
    @PostMapping("/update")
    public void update(@RequestBody H5Info h5Info) {
        h5Service.update(h5Info);
    }

    /**
     * h5-删除
     */
    @Override
    @GetMapping("/delete")
    public void delete(@RequestParam("id") Long id) {
        h5Service.delete(id);
    }

    /**
     * h5-复制
     *
     * @param h5Temp 实体
     * @return success/false
     */
    @Override
    @PostMapping("/copy")
    public H5CopyResp copy(@RequestBody H5Info h5Temp) {
        return h5Service.copy(h5Temp);

    }

    @Override
    @PostMapping("/form/submit")
    public void formSubmit(H5FromSubmitReq req) {
        h5Service.fromSubmit(req);
    }



    @Override
    @ApiOperation("h5-根据活动ID获取")
    @GetMapping("/form/getByActivityId")
    public H5Info getByActivityId(Long mcActivityId) {
        return h5Service.getByActivityId(mcActivityId);
    }

    @Override
    @ApiOperation("h5-获取H5模板列表")
    @PostMapping("/form/template/list")
    public List<H5TemplateInfo> h5TemplateListQuery(@RequestBody H5TemplateListQueryReq req) {
        return h5Service.h5TemplateListQuery(req);
    }

    @Override
    @ApiOperation("h5-CSP查询H5模版列表")
    @PostMapping("/form/template/pageListForCSP")
    public PageResult<ResourcesForm> pageListForCSP(@RequestBody FormPageQuery query) {
        return h5Service.pageListForCSP(query);
    }

    @Override
    @ApiOperation("h5-CSP客户查询H5模版列表")
    @GetMapping("/form/template/getTreeListForCustomer")
    public List<FormManagementTreeResp> getTreeListForCustomer() {
        return h5Service.getTreeListForCustomer();
    }

    @Override
    @ApiOperation("h5-更新H5模版的mssID")
    @GetMapping("/form/template/updateMssID")
    public void updateMssID(@RequestParam("msId")String msId, @RequestParam("mssId")Long mssId) {
        h5Service.updateMssID(msId,mssId);
    }

    @Override
    @ApiOperation("h5-用户购买H5模版后保存到用户的H5列表中")
    @GetMapping("/form/template/saveForCustomer")
    public void saveForCustomer(@RequestParam("msId")Long msId, @RequestParam("userId")String userId, @RequestParam("mssId") Long mssId) {
        h5Service.saveForCustomer(msId,userId,mssId);
    }

    @Override
    @ApiOperation("h5-根据ID删除表单内容")
    @PostMapping("/form/deleteInfo")
    public void formDeleteInfo(@RequestParam("id") Long id) {
        h5Service.formDeleteInfo(id);
    }

    @Override
    @ApiOperation("h5-获取表单列表")
    @PostMapping("/form/queryList")
    public PageResult<H5FormInfo> formQueryList(@RequestBody FormInfoPageQuery query) {
        return h5Service.formQueryList(query);
    }

    @Override
    @ApiOperation("h5-删除和作品之间的关系")
    @PostMapping("/form/deleteMssIDForIds")
    public void deleteMssIDForIds(@RequestParam("h5OfSummeryIdList") List<Long> h5OfSummeryIdList) {
        h5Service.deleteMssIDForIds(h5OfSummeryIdList);
    }

    @Override
    @ApiOperation("h5-根据ID查询所有信息")
    @PostMapping("/form/list/byIds")
    public List<H5Info> byIds(@RequestBody H5InfoListQuery query) {
        return h5Service.byIds(query);
    }

    @Override
    @ApiOperation("h5-对于作品的H5生成备份")
    @PostMapping("/createSummeryH5")
    public Long createSummeryH5(@RequestParam("msId") String msId) {
        return h5Service.createSummeryH5(msId);
    }

    @Override
    @ApiOperation("h5-对于作品的H5获取备份的数据")
    @PostMapping("/getDetailForSummery")
    public H5Info getDetailForSummery(@RequestParam("msId") Long msId) {
        return h5Service.getDetailForSummery(msId);
    }

    @Override
    @ApiOperation("h5-对于作品的H5g更新备份")
    @PostMapping("/updateSummaryH5")
    public void updateSummaryH5(@RequestParam("msId") String msId) {
        h5Service.updateSummaryH5(msId);
    }

    @Override
    @ApiOperation("h5-导入用户的相模型")
    @GetMapping("/form/template/importH5ForCustomer")
    public void importH5ForCustomer(@RequestParam("mssId")Long mssId, @RequestParam("userId")String userId, @RequestParam("name") String name) {
        h5Service.importH5ForCustomer(mssId,userId,name);
    }

    @Override
    @ApiOperation("h5-机器人和5G消息导入表单")
    @GetMapping("/form/template/importH5ForRobotAndMSG")
    public Map<Long, Csp4CustomerFrom> importH5ForRobotAndMSG(@RequestParam("h5Ids") List<Long> h5Ids) {
        return h5Service.importH5ForRobotAndMSG(h5Ids);
    }
}
