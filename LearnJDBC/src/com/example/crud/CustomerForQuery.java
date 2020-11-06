package com.example.crud;

import com.example.bean.Customer;
import com.example.utils.JDBCUtils;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-06 11:20
 * @Description: 查询Customer操作
 */
public class CustomerForQuery {

    @Test
    public void testQuery(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            // 1. 获取连接
            conn = JDBCUtils.getConnection();
            // 2. 预编译sql语句
            String sql = "select id, name, email, birth from customers where id = ?";
            ps = conn.prepareStatement(sql);
            // 3. 填占位符
            ps.setObject(1, 20);
            // 4. 执行查询操作
            resultSet = ps.executeQuery();

            // 5. 处理结果集
            // next()：判断结果集的下一条是否有数据
            if (resultSet.next()) {
                // 获取当前这条数据的各个字段值
                int id = resultSet.getInt(1); // 按字段索引位置来提取值，也是从1开始
                String name = resultSet.getString(2);
                String email = resultSet.getString("email"); // 可以通过字段名或别名来提取值
                Date birth = resultSet.getDate("birth"); // 这个是java.sql.date


                // 将这些字段值封装成对象，也可以用数组或map来存，但是更推荐封装成对象
                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 6. 关闭资源
            JDBCUtils.closeResource(conn, ps, resultSet);
        }



    }

    /**
     * @Date: 2020/11/6 12:32
     * @Description: Customer类的通用查询操作
     *
     * @Param: [sql, args]
     * @return: com.example.bean.Customer
     */        
    public List<Customer> queryForCustomers(String sql, Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            // 1. 获取连接
            conn = JDBCUtils.getConnection();
            // 2. 预编译SQL语句
            ps = conn.prepareStatement(sql);
            // 3. 向占位符填入数据
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i+1, args[i]);
            }

            // 4. 执行查询语句
            resultSet = ps.executeQuery();
            // 5. 处理结果集
            if (resultSet != null){
                // 获取结果集的元数据
                ResultSetMetaData metaData = resultSet.getMetaData();
                // 结果集不为空的时候，才会创建返回的List
                List<Customer> customerList = new ArrayList<>();

                // 循环处理每个数据项
                while(resultSet.next()){
                    // 先创建一个Customer对象，使用空参构造器
                    Customer customer = new Customer();

                    // 循环提取每个字段值，并给Customer对象赋值
                    // metaData.getColumnCount()：从结果集元数据中提取出字段个数
                    for (int i = 0; i < metaData.getColumnCount(); i++) {
                        // 从结果集中，提取字段的值
                        Object columnValue = resultSet.getObject(i + 1);// 由于不知道是什么类型，所以用Object

                        // 从结果集元数据中，提取字段的字段名，与值对应
                        String columnName = metaData.getColumnName(i + 1);

                        // 通过反射给创建的这个customer对象属性赋值
                        // 获取当前的字段对应类中的属性镀锡
                        Field field = Customer.class.getDeclaredField(columnName);
                        // 防止该属性是私有属性导致无法set成功
                        field.setAccessible(true);
                        field.set(customer, columnValue); // 给指定customer对象set指定属性的值
                    }

                    // 完成一个customer的属性填充，就向返回list中添加
                    customerList.add(customer);
                }

                // 6. 关闭资源
                JDBCUtils.closeResource(conn, ps, resultSet);

                // 最后返回查询后封装的数据
                return customerList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // 6. 关闭资源，这个地方也要关闭
            JDBCUtils.closeResource(conn, ps, resultSet);
        }

        return null; // 没有查询到数据返回null，一定要放在最后
    }


    // 测试通用查询类
    @Test
    public void testQueryForCustomers(){
//        String sql = "select id, name, birth, email from customers where id = ?";
        String sql = "select id, name, birth, email from customers";
        List<Customer> customerList = queryForCustomers(sql);
        System.out.println(customerList);
    }
}
