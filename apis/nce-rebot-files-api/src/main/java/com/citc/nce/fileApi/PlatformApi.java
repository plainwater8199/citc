package com.citc.nce.fileApi;


import com.citc.nce.dto.*;
import com.citc.nce.filecenter.vo.TemplateOwnershipReflect;
import com.citc.nce.vo.AccountResp;
import com.citc.nce.vo.ExamResp;
import com.citc.nce.vo.StatisticsResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "PlatformApi",value = "robot-files-service", url = "${robotFile:}")
public interface PlatformApi {
    /**
     *
     * 审核接口
     */
    @PostMapping("/{chatbotId}/delivery/mediaStatus")
    void mediaStatus(@RequestBody FileAccept fileAccept, @PathVariable("chatbotId")String chatbotId);

    @PostMapping("/delivery/mediaStatus")
    void mediaStatus(@RequestBody FileAccept fileAccept);

    /**
     *  账户列表接口
     *
     */
    @PostMapping("/material/account/list")
    List<AccountResp> getAccountList();

    @PostMapping("/material/used")
    Boolean fileUsed(@RequestBody FileReq fileReq);
    @PostMapping("/material/usedByAll")
    Boolean fileUsedAllPossible(@RequestBody FileReq fileReq);
    @PostMapping("/material/getFileTid")
    FileAccept getFileTid(@RequestBody FileTidReq fileTidReq);

    @PostMapping("/material/examine")
    void examine(@RequestBody FileTidReq fileTidReq);

    @PostMapping("/material/deleteAuditRecord")
    void deleteAuditRecord(@RequestBody FileExamineDeleteReq fileExamineDeleteReq);

    @PostMapping("/material/saveExam")
    void saveExam(@RequestBody UpFileReq upFileReq);

    @PostMapping("/material/verification")
    Boolean verification(@RequestBody VerificationReq verificationReq);

    /**
     * 保存模板时检查是否所有素材有共有的运营商服务商交集
     * @param verificationReq
     * @return
     */
    @PostMapping("/material/verificationShare")
    public  List<TemplateOwnershipReflect> verificationShare(@RequestBody VerificationReq verificationReq) ;
        /**
         * 统计素材数量接口
         * @param fileTidReq  运营商
         * @return 数量集合
         */
    @PostMapping("/material/statistics")
    List<StatisticsResp> statistics(@RequestBody FileTidReq fileTidReq);


    /**
     * 统计素材数量接口
     * @param verificationReq  运营商
     * @return 数量集合
     */
    @PostMapping("/material/findExamByUuid")
    List<ExamResp> findExamByUuid(@RequestBody VerificationReq verificationReq);
}
