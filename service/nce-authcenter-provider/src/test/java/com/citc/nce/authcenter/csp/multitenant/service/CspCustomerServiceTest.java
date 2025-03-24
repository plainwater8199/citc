package com.citc.nce.authcenter.csp.multitenant.service;

import com.citc.nce.authcenter.csp.multitenant.dao.CspCustomerMapper;
import com.citc.nce.authcenter.csp.multitenant.entity.CspCustomer;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author jcrenc
 * @since 2024/2/27 11:47
 */
@SpringBootTest
class CspCustomerServiceTest {
    @Autowired
    private CspCustomerMapper cspCustomerMapper;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    void modifyTable() {


//        List<CspCustomer> cspCustomers = cspCustomerMapper.selectList();
//        for (CspCustomer cspCustomer : cspCustomers) {
//            System.out.println(cspCustomer);
//        }
        cspCustomerMapper.deletePayTypeColum();
        cspCustomerMapper.addPayTypeColum();
//        SqlSession sqlSession = sqlSessionFactory.openSession(false);
//        try (Connection connection = sqlSession.getConnection()) {
//            connection.setSchema("auth_0126");
//            connection.nativeSQL("alter table csp_customer add column pay_type  tinyint default 0 comment '消息付费方式  0后付费 1预付费';");
//            connection.commit();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

    }
}