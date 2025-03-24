package com.citc.nce.robotfile.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.dto.GroupReq;
import com.citc.nce.robotfile.entity.GroupDo;
import com.citc.nce.vo.GroupResp;

import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: IGroupService
 */
public interface IGroupService extends IService<GroupDo> {


    List<GroupResp> selectAll();

    void saveGroup(String groupName);

    void updateGroup(List<GroupReq> vos);


    List<GroupResp> manage();
}
