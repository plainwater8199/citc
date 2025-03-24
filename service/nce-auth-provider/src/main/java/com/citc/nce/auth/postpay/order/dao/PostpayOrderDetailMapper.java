package com.citc.nce.auth.postpay.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.postpay.order.entity.PostpayOrderDetail;
import com.citc.nce.auth.postpay.order.vo.PostpayOrderDetailVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jcrenc
 * @since 2024/3/7 16:15
 */
public interface PostpayOrderDetailMapper extends BaseMapper<PostpayOrderDetail> {
    List<PostpayOrderDetailVo> selectOrderDetail(@Param("orderId") String orderId);
}
