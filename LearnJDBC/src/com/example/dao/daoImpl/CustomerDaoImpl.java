package com.example.dao.daoImpl;

import com.example.bean.Customer;
import com.example.dao.BaseDao;
import com.example.dao.CustomerDao;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-08 8:50
 * @Description:
 *
 */
public class CustomerDaoImpl extends BaseDao<Customer> implements CustomerDao {
    @Override
    public void insert(Connection conn, Customer customer) {
        String sql = "insert into customers(name, birth, email) values(?, ?, ?);";
        // 使用通用的增删改操作
        commonUpdate(conn, sql, customer.getName(), customer.getBirth(), customer.getEmail());
    }

    @Override
    public void deleteById(Connection conn, int id) {
        String sql = "delete from customers where id = ?;";
        // 也使用通用的增删改操作
        commonUpdate(conn, sql, id);
    }

    @Override
    public void update(Connection conn, Customer customer) {
        String sql = "update customers set name = ?, birth = ?, email = ? where id = ?;";
        // 通用使用通用的更新操作
        commonUpdate(conn, sql, customer.getName(), customer.getBirth(), customer.getEmail(), customer.getId());
    }

    @Override
    public Customer getCustomerById(Connection conn, int id) {
        String sql = "select id, name, email, birth from customers where id = ?";
        List<Customer> customerList = commonQuery(conn, sql, id);
        return customerList.get(0);
    }

    @Override
    public List<Customer> getAll(Connection conn) {
        // 获取全部的customer
        String sql = "select id, name, email, birth from customers;";
        return commonQuery(conn, sql);
    }

    @Override
    public Long getCount(Connection conn) {
        String sql = "select count(*) from customers;";
        return getValue(conn, sql);
    }

    @Override
    public Date getMaxBirth(Connection conn) {
        String sql = "select max(birth) from customers;";
        return getValue(conn, sql);
    }
}
