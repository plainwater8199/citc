package com.citc.nce.authcenter.processrecord;

import cn.hutool.core.collection.CollectionUtil;
import com.citc.nce.authcenter.auth.service.AdminAuthService;
import com.citc.nce.authcenter.auth.service.AuthService;
import com.citc.nce.authcenter.auth.vo.req.GetUsersInfoReq;
import com.citc.nce.authcenter.auth.vo.resp.AdminUserResp;
import com.citc.nce.authcenter.auth.vo.resp.UserInfo;
import com.citc.nce.misc.record.ProcessingRecordApi;
import com.citc.nce.misc.record.req.BusinessIdReq;
import com.citc.nce.misc.record.resp.ProcessingRecordResp;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bydud
 * @since 11:32
 */
@RestController
@AllArgsConstructor
public class ProcessRecordController implements ProcessRecordApi {
    private final ProcessingRecordApi miscRecordApi;
    private final AdminAuthService adminAuthService;
    private final AuthService authService;

    @Override
    public List<ProcessingRecordResp> findProcessingRecordList(BusinessIdReq req) {
        List<ProcessingRecordResp> recordList = miscRecordApi.findProcessingRecordList(req);
        if (CollectionUtil.isEmpty(recordList)) return recordList;
        //查询操作人名称
        Set<String> userIds = recordList.stream().map(ProcessingRecordResp::getProcessingUserId).collect(Collectors.toSet());
        Map<String, String> adminMap = adminAuthService.getAdminUserByUserId(userIds).stream()
                .collect(Collectors.toMap(AdminUserResp::getUserId, AdminUserResp::getFullName));
        GetUsersInfoReq cspReq = new GetUsersInfoReq();
        cspReq.setUserIds(new ArrayList<>(userIds));
        List<UserInfo> userBaseInfoByUserIds = authService.getUserBaseInfoByUserIds(cspReq);
        Map<String, String> cspMap = new HashMap<>();
        if(!CollectionUtil.isEmpty(userBaseInfoByUserIds)){
            cspMap = userBaseInfoByUserIds.stream()
                    .collect(Collectors.toMap(UserInfo::getUserId, UserInfo::getName));
        }

        for (ProcessingRecordResp resp : recordList) {
            resp.setProcessingUserName(adminMap.get(resp.getProcessingUserId()));
            if(resp.getProcessingUserName() == null && cspMap.containsKey(resp.getProcessingUserId())){
                resp.setProcessingUserName(cspMap.get(resp.getProcessingUserId()));
            }

        }
        return recordList;
    }
}
