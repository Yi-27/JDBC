package com.example.dao;

import com.example.bean.Customer;

import java.sql.Connection;
import java.util.List;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-08 8:31
 * @Description:
 *  此接口用于规范针对customers表的常用操作
 */
public interface CustomerDao {

    /**
     * @Date: 2020/11/8 8:41
     * @Description:
     *  Customers表通用的插入操作，这里都是单次插入
     * @Param: [conn, customer]
     * @return: void
     */
    void insert(Connection conn, Customer customer);

    /**
     * @Date: 2020/11/8 8:41
     * @Description:
     *  删除指定的customer
     *
     * @Param: [conn, id]
     * @return: void
     */
    void deleteById(Connection conn, int id);

    /**
     * @Date: 2020/11/8 8:41
     * @Description:
     *  更新指定的customer
     *
     * @Param: [conn, customer]
     * @return: void
     */
    void update(Connection conn, Customer customer);

    /**
     * @Date: 2020/11/8 8:41
     * @Description:
     *  获取指定customer
     *
     * @Param: [conn, id]
     * @return: com.example.bean.Customer
     */
    Customer getCustomerById(Connection conn, int id);

    /**
     * @Date: 2020/11/8 8:41
     * @Description:
     *  获取全部customer
     *
     * @Param: [conn]
     * @return: java.util.List<com.example.bean.Customer>
     */
    List<Customer> getAll(Connection conn);

    /**
     * @Date: 2020/11/8 8:41
     * @Description:
     *  返回数据表中的数据的条目数
     *
     * @Param: [conn]
     * @return: java.lang.Long
     */
    Long getCount(Connection conn);

    /**
     * @Date: 2020/11/8 8:41
     * @Description:
     *  返回数据表中最大的生日（纯看数，不是看日期）
     *
     * @Param: [conn]
     * @return: java.sql.Date
     */
    java.sql.Date getMaxBirth(Connection conn);
}
