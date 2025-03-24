package com.citc.nce.mybatis.core.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.mybatis.core.util.MyBatisUtils;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/4/25 14:21
 * @Version: 1.0
 * @Description:
 */
public interface BaseMapperX<T> extends BaseMapper<T> {

    /**
     * 分页查询，自定义分页参数对象和返回值，避免直接和mybatis plus耦合
     *
     * @param pageParam
     * @param queryWrapper
     * @return
     */
    default PageResult<T> selectPage(PageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        // MyBatis Plus 查询
        IPage<T> mpPage = MyBatisUtils.buildPage(pageParam);
        selectPage(mpPage, queryWrapper);
        // 转换返回
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
    }

    /**
     * 获取分页的数据
     *
     * @param pageParam
     * @param queryWrapper
     * @return
     */
    default List<T> selectPageList(PageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        // MyBatis Plus 查询
        pageParam.setCount(false);
        IPage<T> mpPage = MyBatisUtils.buildPage(pageParam);
        selectPage(mpPage, queryWrapper);
        // 转换返回
        return mpPage.getRecords();
    }

    /**
     * 查询column1=column2的数据
     *
     * @param column1 字段1
     * @param column2 字段2
     * @return
     */
    default List<T> selectListByColumnEq(String column1, String column2) {
        LambdaQueryWrapper<T> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.apply(column1 + " = " + column2);
        List<T> ts = selectList(lambdaQueryWrapper);
        return ts;
    }

    /**
     * 批量插入,目前只支持MySQL,采用mysql批量插入语法:
     * insert into user(id, name, age) values (1, "a", 17), (2,"b", 18)
     * 需要自己控制批量插入的集合大小
     *
     * @param list
     * @return
     */
    int insertBatch(List<T> list);

    /**
     * 根据主键字段批量更新
     *
     * @param list
     * @return
     */
    int updateBatch(List<T> list);

    /**
     * 一个条件等值查询
     *
     * @param field
     * @param value
     * @return
     */
    default T selectOne(String field, Object value) {
        return selectOne(new QueryWrapper<T>().eq(field, value));
    }

    default T selectOne(SFunction<T, ?> field, Object value) {
        return selectOne(new LambdaQueryWrapper<T>().eq(field, value));
    }

    /**
     * 两个条件等值查询
     *
     * @param field1
     * @param value1
     * @param field2
     * @param value2
     * @return
     */
    default T selectOne(String field1, Object value1, String field2, Object value2) {
        return selectOne(new QueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return selectOne(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    /**
     * 查询记录总数
     *
     * @return
     */
    default Long selectCount() {
        return selectCount(new QueryWrapper<T>());
    }

    /**
     * 根据指定的一个条件查询记录总数
     *
     * @param field
     * @param value
     * @return
     */
    default Long selectCount(String field, Object value) {
        return selectCount(new QueryWrapper<T>().eq(field, value));
    }

    default Long selectCount(SFunction<T, ?> field, Object value) {
        return selectCount(new LambdaQueryWrapper<T>().eq(field, value));
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    default List<T> selectList() {
        return selectList(new QueryWrapper<>());
    }

    /**
     * 根据一个条件等值查询
     *
     * @param field
     * @param value
     * @return
     */
    default List<T> selectList(String field, Object value) {
        return selectList(new QueryWrapper<T>().eq(field, value));
    }

    default List<T> selectList(SFunction<T, ?> field, Object value) {
        return selectList(new LambdaQueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据一个条件in查询
     *
     * @param field
     * @param values
     * @return
     */
    default List<T> selectList(String field, Collection<?> values) {
        return selectList(new QueryWrapper<T>().in(field, values));
    }

    default List<T> selectList(SFunction<T, ?> field, Collection<?> values) {
        return selectList(new LambdaQueryWrapper<T>().in(field, values));
    }

    /**
     * 根据id批量删除
     * @param ids
     * @return
     */
    default int logicDeleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("deleted", 1)
                .set("delete_time", new Date())
                .in("id", ids);
        return update(null, updateWrapper);
    }

}
