package com.citc.nce.robot.api;

import com.citc.nce.robot.domain.massSegment.MassSegmentDetail;
import com.citc.nce.robot.domain.massSegment.MassSegmentVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author jcrenc
 * @since 2024/2/21 10:39
 */
@FeignClient(value = "im-service", contextId = "massSegmentApi", url = "${im:}")
public interface MassSegmentApi {

    @GetMapping("/massSegment/list/groupCustomOperator")
    Map<String, List<MassSegmentDetail>> listCustomGroupOperator();

    @GetMapping("/massSegment/queryOperator")
    String queryOperator(@RequestParam("phoneSegment") String phoneSegment);


//water1118    String getOperatorStringByPhone(@RequestParam("phoneSegment") String phoneSegment);

    @GetMapping("/massSegment/queryAllSegment")
    Map<String, String> queryAllSegment();

    @PostMapping("/massSegment/save")
    void saveCustom(@RequestBody @Valid MassSegmentVo entity);

    @PostMapping("/massSegment/removeById/{id}")
    void removeById(@PathVariable("id") Long id);
}
