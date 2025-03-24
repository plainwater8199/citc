package com.citc.nce.h5.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.h5.dao.H5DataSourceMapper;
import com.citc.nce.h5.entity.H5DataSourceDo;
import com.citc.nce.h5.service.H5DataSourceService;
import com.citc.nce.h5.vo.H5DataSourceInfo;
import com.citc.nce.h5.vo.req.H5DataSourceQueryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiawenzhu
 * @since 2023-06-15
 */
@Service
public class H5DataSourceServiceImpl extends ServiceImpl<H5DataSourceMapper, H5DataSourceDo> implements H5DataSourceService , IService<H5DataSourceDo> {

    @Override
    public PageResult<H5DataSourceInfo> page(H5DataSourceQueryVO dataSourceVo) {
        Page<H5DataSourceDo> page = new Page<>(dataSourceVo.getPageNo(), dataSourceVo.getPageSize());
        page(page, new LambdaQueryWrapper<H5DataSourceDo>()
                .eq(H5DataSourceDo::getCreator, SessionContextUtil.getUserId())
                .orderByDesc(H5DataSourceDo::getCreateTime)
        );
        List<H5DataSourceInfo> result = page.getRecords().stream().map(item -> {
            H5DataSourceDo dataSourceDo = new H5DataSourceDo();
            BeanUtils.copyProperties(item, dataSourceDo);
            H5DataSourceInfo dataSourceInfo = new H5DataSourceInfo();
            BeanUtils.copyProperties(dataSourceDo, dataSourceInfo);
            return dataSourceInfo;
        }).collect(Collectors.toList());
        return new PageResult<>(result, page.getTotal());
    }

    @Override
    public void add(H5DataSourceInfo dataSource) {
        H5DataSourceDo dataSourceDo = new H5DataSourceDo();
        BeanUtils.copyProperties(dataSource, dataSourceDo);
        dataSourceDo.setCreator(SessionContextUtil.getUserId());
        dataSourceDo.setUpdater(SessionContextUtil.getUserId());
        dataSourceDo.setCreateTime(new Date());
        dataSourceDo.setUpdateTime(new Date());
        this.save(dataSourceDo);
    }

    @Override
    public H5DataSourceInfo previewData(Long id) {
       // 校验数据源是否存在
        H5DataSourceDo dataSourceDo = checkDataSource(id);
        H5DataSourceInfo dataSourceInfo = new H5DataSourceInfo();
        BeanUtils.copyProperties(dataSourceDo, dataSourceInfo);
        return dataSourceInfo;
    }

    @Override
    public void delete(Long id) {
        // 校验数据源是否存在
        H5DataSourceDo dataSourceDo = checkDataSource(id);
        this.removeById(dataSourceDo.getId());
    }



    @Override
    public void update(H5DataSourceInfo dataSource) {
        // 校验数据源是否存在
        H5DataSourceDo dataSourceDo = checkDataSource(dataSource.getId());
        dataSourceDo.setField(dataSource.getField());
        dataSourceDo.setUpdateTime(new Date());
        dataSourceDo.setName(dataSource.getName());
        dataSourceDo.setData(dataSource.getData());
        dataSourceDo.setType(dataSource.getType());
        dataSourceDo.setUpdateTime(new Date());
        dataSourceDo.setUpdater(SessionContextUtil.getUserId());
        this.updateById(dataSourceDo);

    }


    private H5DataSourceDo checkDataSource(Long id) {
        H5DataSourceDo one = this.lambdaQuery().eq(H5DataSourceDo::getId, id).eq(H5DataSourceDo::getCreator, SessionContextUtil.getUserId()).one();
        if(one == null){
            throw new BizException("数据源不存在！");
        }
        return one;
    }

}
