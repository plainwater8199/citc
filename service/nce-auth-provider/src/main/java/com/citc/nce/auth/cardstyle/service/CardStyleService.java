package com.citc.nce.auth.cardstyle.service;

import com.citc.nce.auth.cardstyle.vo.*;
import com.citc.nce.common.core.pojo.PageParam;

import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 16:30
 * @Version: 1.0
 * @Description:
 */
public interface CardStyleService {


    PageResultResp getCardStyles(PageParam pageParam);

    Long saveCardStyle(CardStyleReq cardStyleReq);

    int updateCardStyle(CardStyleEditReq cardStyleEditReq);

    int delCardStyleById(CardStyleOneReq cardStyleOneReq);

    CardStyleResp getCardStyleById(CardStyleOneReq cardStyleOneReq);

    List<CardStyleTreeResp> getTreeList();

    CardStyleResp getCardStyleByIdInner(CardStyleOneReq cardStyleOneReq);
}
