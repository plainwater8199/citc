package com.citc.nce.auth.user.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/18 11:05
 * @Version 1.0
 * @Description:部分sql不需要执行全局逻辑删除使用sql拦截器进行处理
 */
@Slf4j
@Component
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class SqlModifyInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        //通过MetaObject优雅访问对象的属性，这里是访问statementHandler的属性;：MetaObject是Mybatis提供的一个用于方便、
        //优雅访问对象属性的对象，通过它可以简化代码、不需要try/catch各种reflect异常，同时它支持对JavaBean、Collection、Map三种类型对象的操作。
        MetaObject metaObject = MetaObject
                .forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
                        new DefaultReflectorFactory());
        //先拦截到RoutingStatementHandler，里面有个StatementHandler类型的delegate变量，其实现类是BaseStatementHandler，然后就到BaseStatementHandler的成员变量mappedStatement
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        //id为执行的mapper方法的全路径名，如com.uv.dao.UserMapper.insertUser
        String id = mappedStatement.getId();
        //log.info("id为执行的mapper方法的全路径名:" + id);
        //if (SqlInterceptor.sqlList.contains(id)) {
        //    //sql语句类型 select、delete、insert、update
        //    String sqlCommandType = mappedStatement.getSqlCommandType().toString();
        //    log.info("sql语句类型:" + sqlCommandType);
        //    BoundSql boundSql = statementHandler.getBoundSql();
        //    //获取到原始sql语句
        //    String sql = boundSql.getSql();
        //    log.info("拦截的sql语句:" + sql);
        //    //可以先打印出原始的sql语句，然后根据实际情况修改，我个人建议是下面这样修改
        //    //只在原sql上添加条件而不是删除条件
        //    sql = sql.replace("deleted = 0", "deleted=0 OR deleted=1 ");
        //    sql = sql.replace("deleted=0", "deleted=0 OR deleted=1 ");
        //    log.info("修改之后的sql：" + sql);
        //    //通过反射修改sql语句
        //    //下面类似于替换sql
        //    Field field = boundSql.getClass().getDeclaredField("sql");
        //    field.setAccessible(true);
        //    field.set(boundSql, sql);
        //}
        return invocation.proceed();
    }
}