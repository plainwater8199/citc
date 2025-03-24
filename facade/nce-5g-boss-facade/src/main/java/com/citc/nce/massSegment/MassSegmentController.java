package com.citc.nce.massSegment;

import com.citc.nce.robot.api.MassSegmentApi;
import com.citc.nce.robot.domain.massSegment.MassSegmentDetail;
import com.citc.nce.robot.domain.massSegment.MassSegmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bydud
 * @since 2024/5/7
 */

@RestController
@Api(value = "MassSegmentController", tags = "自定义号段设置")
public class MassSegmentController {
    @Autowired
    private MassSegmentApi massSegmentApi;

    @GetMapping("/massSegment/list/groupCustomOperator")
    @ApiOperation("查询已设置的号段")
    public Map<String, List<MassSegmentDetail>> listCustomGroupOperator() {
        return massSegmentApi.listCustomGroupOperator();
    }

    @PostMapping("/massSegment/save")
    @ApiOperation("保存自定义号段")
    public void saveCustom(@RequestBody @Valid MassSegmentVo entity) {
        massSegmentApi.saveCustom(entity);
    }

    @PostMapping("/massSegment/removeById/{id}")
    @ApiOperation("删除自定义号段")
    public void delById(@PathVariable("id") Long id) {
        massSegmentApi.removeById(id);
    }
}
