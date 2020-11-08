package com.example.test;

import com.example.bean.Customer;
import com.example.dao.CustomerDao;
import com.example.dao.daoImpl.CustomerDaoImpl;
import com.example.utils.JDBCPoolUtils;
import com.example.utils.JDBCUtils;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-08 9:49
 * @Description:
 *      这里懒得写try-catch-finally，所以都抛异常了
 *      但是实际上不应该这样写，而是去捕获并处理
 */
public class CustomerDaoImplTest {
    CustomerDao customerDao = new CustomerDaoImpl();


    @Test
    public void getAll() throws SQLException, IOException, ClassNotFoundException {
        Connection conn = JDBCUtils.getConnection();

        List<Customer> customers = customerDao.getAll(conn);
        customers.forEach(System.out::println);

        JDBCUtils.closeResource(conn, null);
    }

    @Test
    public void insert() throws SQLException, IOException, ClassNotFoundException, ParseException {
        Connection conn = JDBCUtils.getConnection();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date parseDate = sdf.parse("2020-11-07");
        Customer customer = new Customer(22, "长泽雅美", "yamei@qq.com", new Date(parseDate.getTime()));
        customerDao.insert(conn, customer);

        JDBCUtils.closeResource(conn, null);
    }

    @Test
    public void deleteById() throws SQLException, IOException, ClassNotFoundException {
        Connection conn = JDBCUtils.getConnection();

        customerDao.deleteById(conn, 4);

        JDBCUtils.closeResource(conn, null);
    }

    @Test
    public void update() throws SQLException, IOException, ClassNotFoundException, ParseException {
        Connection conn = JDBCUtils.getConnection();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date parseDate = sdf.parse("2020-11-08");
        Customer customer = new Customer(21, "石原里美", "shiyuan@qq.com", new Date(parseDate.getTime()));
        customerDao.update(conn, customer);

        JDBCUtils.closeResource(conn, null);
    }

    @Test
    public void getCustomerById() throws Exception {
//        Connection conn = JDBCUtils.getConnection();
//        Connection conn = JDBCPoolUtils.getConnectionFromC3P0(); // 使用c3p0数据库连接池获取连接
//        Connection conn = JDBCPoolUtils.getConnectionFromDBCP(); // 使用dbcp数据库连接池获取连接
        Connection conn = JDBCPoolUtils.getConnectionFromDruid(); // 使用Druid数据库连接池获取连接

        Customer customer = customerDao.getCustomerById(conn, 20);
        System.out.println(customer);

        JDBCUtils.closeResource(conn, null);
    }

    @Test
    public void getCount() throws SQLException, IOException, ClassNotFoundException {
        Connection conn = JDBCUtils.getConnection();

        Long count = customerDao.getCount(conn); // 表中记录数
        System.out.println(count);

        JDBCUtils.closeResource(conn, null);
    }

    @Test
    public void getMaxBirth() throws SQLException, IOException, ClassNotFoundException {
        Connection conn = JDBCUtils.getConnection();

        java.sql.Date maxBirth = customerDao.getMaxBirth(conn);
        System.out.println(maxBirth.toString()); // 最大生日时间

        JDBCUtils.closeResource(conn, null);
    }
}