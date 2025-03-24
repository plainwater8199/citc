package com.citc.nce.materialSquare.service;

import com.citc.nce.materialSquare.entity.MsManageSuggest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.materialSquare.vo.suggest.SuggestAdd;
import com.citc.nce.materialSquare.vo.suggest.SuggestChangeNum;
import com.citc.nce.materialSquare.vo.suggest.SuggestListOrderNum;
import com.citc.nce.materialSquare.vo.suggest.req.SuggestOrderReq;
import com.citc.nce.materialSquare.vo.suggest.resp.SuggestListResp;

import java.util.List;

/**
 * <p>
 * 素材广场_后台管理_首页推荐	 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-05-15 10:05:47
 */
public interface IMsManageSuggestService extends IService<MsManageSuggest> {

    void putSuggest(SuggestAdd suggestAdd);

    void changeOrderNum(SuggestOrderReq req);

    SuggestListResp listOrderNum();

    void setTop(SuggestAdd suggestAdd);

    void cleanTop();

    List<MsManageSuggest> listByMsActivityIds(List<Long> activityIds);
}
