package com.citc.nce.customcommand.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.customcommand.entity.CustomCommand;
import com.citc.nce.customcommand.vo.*;
import com.citc.nce.customcommand.vo.resp.SearchListForMSResp;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 自定义指令 服务类
 * </p>
 *
 * @author jcrenc
 * @since 2023-11-09 02:53:48
 */
public interface ICustomCommandService extends IService<CustomCommand> {

    /**
     * 新增自定义指令
     */
    void add(CustomCommandAddReq addReq);

    /**
     * 搜索所有状态指令
     */
    PageResult<CustomCommandSimpleVo> searchCommand(CustomCommandSearchReq searchReq);

    /**
     * 搜索已发布的指令
     */
    PageResult<CustomCommandSimpleVo> searchPublishCommand(CustomCommandSearchReq searchReq);

    /**
     * 发布指令
     * @param id 指令ID
     */
    void publish(Long id);

    /**
     * 编辑指令
     */
    void edit(CustomCommandEditReq editReq);

    /**
     * 删除指令
     * @param id 指令ID
     */
    void delete(Long id);

    /**
     * 还原指令
     * @param id 指令ID
     */
    void restore(Long id);

    /**
     * 修改指令可用状态
     * @param id 指令ID
     * @param active 可用状态，true为可用
     */
    void active(Long id, Boolean active);

    /**
     * 查询指令详情
     * @param id 指令ID
     */
    CustomCommandDetailVo getDetail(Long id);

    /**
     * 查询我可用的指令
     */
    PageResult<MyAvailableCustomCommandVo> getMyAvailableCommand(MyAvailableCustomCommandReq req);

    Long countMyCommand();

    PageResult<MyAvailableCustomCommandVo> getMyCommand(MyAvailableCustomCommandReq req);

    CustomCommandDetailVo getDetailByUuid(String uuid);

    /**
     * 使用自定义命令 再保存
     *
     */
    void useCommand(CustomCommandDetailVo commandDetailVo);

    SearchListForMSResp searchListForMS(SearchListForMSReq req);

    CustomCommandDetailVo getByUuid(String uuid);

    void updateMssID(String id, Long mssId);

    void deleteMssIDForIds(List<Long> mssIDList);

    CustomCommandDetailVo getByMsId(String id);
}
