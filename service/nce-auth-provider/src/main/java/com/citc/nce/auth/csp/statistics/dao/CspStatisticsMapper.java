package com.citc.nce.auth.csp.statistics.dao;

import com.citc.nce.auth.csp.customer.vo.CustomerProvinceResp;
import com.citc.nce.auth.csp.statistics.dto.CspGetTotalChatbot;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsIndustryTypeResp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/13 10:32
 */
@Mapper
public interface CspStatisticsMapper {

    Integer getTotalCsp();

    Integer getYesterdayCsp();

    Integer getTodayCsp();

    List<CspGetTotalChatbot> getTotalChatbot();

    List<CustomerProvinceResp> getCspProvince();


}
