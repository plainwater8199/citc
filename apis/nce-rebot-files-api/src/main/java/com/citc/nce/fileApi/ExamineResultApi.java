package com.citc.nce.fileApi;

import com.citc.nce.vo.ExamineResultResp;
import com.citc.nce.vo.ExamineResultVo;
import com.citc.nce.vo.FileExamineStatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;


/**
 * @Author: ping chen
 */
@FeignClient(contextId = "ExamineResultApi", value = "robot-files-service", url = "${robotFile:}")
public interface ExamineResultApi {

    @PostMapping(value = "/examineResult/query")
    ExamineResultVo queryExamineResult(@RequestBody @Valid ExamineResultResp examineResultResp);


    @PostMapping("/examineResult/updateMaterialFromChatbotUpdate")
    Long updateMaterialFromChatbotUpdate(@RequestParam("oldChatbotId") String oldChatbotId);

    @GetMapping("/examine/status/batch")
    List<FileExamineStatusDto> queryExamineResultBatch(@RequestParam("uuids") List<String> uuids);

}
