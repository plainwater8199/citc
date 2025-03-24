package com.citc.nce.filecenter.service;

import com.citc.nce.filecenter.entity.AvailableQuantity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.filecenter.vo.AccountDetails;

import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月18日15:12:01
 * @Version: 1.0
 * @Description: IAvailableQuantityService
 */
public interface IAvailableQuantityService extends IService<AvailableQuantity> {

    List<AccountDetails> getDetails();
}
