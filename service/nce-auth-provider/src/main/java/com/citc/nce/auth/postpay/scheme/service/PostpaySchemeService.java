package com.citc.nce.auth.postpay.scheme.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.postpay.scheme.dao.PostpaySchemeMapper;
import com.citc.nce.auth.postpay.scheme.entity.PostpayScheme;
import com.citc.nce.auth.postpay.scheme.entity.PostpaySchemeConfig;
import com.citc.nce.auth.postpay.scheme.vo.SchemeAddVo;
import com.citc.nce.auth.postpay.scheme.vo.SchemeListVo;
import com.citc.nce.auth.postpay.scheme.vo.SchemeSearchVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum.*;
import static com.citc.nce.auth.utils.MsgPaymentUtils.MSG_PAYMENT_OPERATORS;
import static com.citc.nce.auth.utils.MsgPaymentUtils.checkConfiguredOperators;

/**
 * @author jcrenc
 * @since 2024/2/28 17:37
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PostpaySchemeService extends ServiceImpl<PostpaySchemeMapper, PostpayScheme> implements IService<PostpayScheme> {
    private final PostpaySchemeConfigService schemeConfigService;

    /**
     * 新增后付费方案
     *
     * @param addVo
     */
    @Transactional(rollbackFor = Exception.class)
    public void addScheme(SchemeAddVo addVo) {
        BaseUser user = SessionContextUtil.getLoginUser();
        String name = addVo.getName();
        if (schemeNameExists(user.getCspId(), name))
            throw new BizException("方案名称重复");
        List<SchemeAddVo.Config> configs = addVo.getConfigs();
        Set<CSPOperatorCodeEnum> configuredOperators = configs.stream()
                .map(SchemeAddVo.Config::getOperator)
                .collect(Collectors.toSet());
        checkConfiguredOperators(configuredOperators);
        PostpayScheme postpayScheme = new PostpayScheme().setName(name);
        this.save(postpayScheme);
        schemeConfigService.addSchemeConfig(postpayScheme.getId(), configs);
    }

    /**
     * 分页查询方案
     *
     * @param searchVo 查询参数
     */
    public PageResult<SchemeListVo> queryScheme(SchemeSearchVo searchVo) {
        Page<PostpayScheme> schemePage = this.lambdaQuery()
                .like(StringUtils.isNotBlank(searchVo.getName()), PostpayScheme::getName, searchVo.getName())
                .eq(PostpayScheme::getCreator,SessionContextUtil.getLoginUser().getCspId())
                .orderByDesc(PostpayScheme::getCreateTime)
                .page(new Page<>(searchVo.getPageNo(), searchVo.getPageSize()));
        if (schemePage.getTotal() == 0)
            return PageResult.empty();
        List<SchemeListVo> schemeListVos = schemePage.getRecords().stream()
                .map(postpayScheme -> {
                    Long schemeId = postpayScheme.getId();
                    SchemeListVo schemeListVo = new SchemeListVo()
                            .setId(schemeId)
                            .setName(postpayScheme.getName())
                            .setCreateTime(postpayScheme.getCreateTime());
                    List<PostpaySchemeConfig> configs = schemeConfigService.lambdaQuery()
                            .eq(PostpaySchemeConfig::getSchemeId, postpayScheme.getId())
                            .list();
                    schemeListVo.setConfigMap(new HashMap<>(MSG_PAYMENT_OPERATORS.size()));
                    for (PostpaySchemeConfig config : configs) {
                        SchemeListVo.Config configVo = new SchemeListVo.Config()
                                .setTextMessagePrice(config.getTextMessagePrice())
                                .setRichMessagePrice(config.getRichMessagePrice())
                                .setConversionPrice(config.getConversionPrice())
                                .setFallbackPrice(config.getFallbackPrice());
                        schemeListVo.getConfigMap().put(config.getOperator(), configVo);
                    }
                    return schemeListVo;
                })
                .collect(Collectors.toList());
        return new PageResult<>(schemeListVos, schemePage.getTotal());
    }


    /**
     * 删除方案
     *
     * @param id 要删除的方案id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteScheme(Long id) {
        BaseUser user = SessionContextUtil.getLoginUser();
        boolean removed = this.remove(lambdaQuery()
                .eq(PostpayScheme::getCreator, user.getCspId())
                .eq(PostpayScheme::getId, id)
                .getWrapper());
        if (!removed)
            throw new BizException("删除失败");
    }








    /*------------------------tool method------------------------------*/


    /**
     * 检查该csp下方案名称是否重复
     */
    private boolean schemeNameExists(String creator, String name) {
        return lambdaQuery()
                .eq(PostpayScheme::getCreator, creator)
                .eq(PostpayScheme::getName, name)
                .exists();
    }
}
