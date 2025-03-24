package com.citc.nce.fileApi;

import com.citc.nce.dto.GroupNameReq;
import com.citc.nce.dto.GroupReq;
import com.citc.nce.dto.PageReq;
import com.citc.nce.vo.GroupResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日17:50:49
 * @Version: 1.0
 * @Description: UpFileDto
 */
@FeignClient(contextId = "group",value = "robot-files-service", url = "${robotFile:}")
public interface GroupApi {

    @PostMapping(value = "/material/group/save")
    void saveGroup(@RequestBody GroupNameReq req);

    @PostMapping(value = "/material/group/list")
    List<GroupResp> selectAllGroups();

    @PostMapping(value = "/material/group/update")
    void updateGroup(@RequestBody List<GroupReq> vos);

    @PostMapping(value = "/material/group/manage")
    List<GroupResp> manageGroup(@RequestBody PageReq pageReq);
}
