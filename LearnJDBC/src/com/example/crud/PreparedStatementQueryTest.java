package com.example.crud;

import com.example.bean.Customer;
import com.example.bean.Order;
import com.example.utils.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-06 17:19
 * @Description: 使用PreparedStatement实现针对不同表的通用的查询操作
 */
public class PreparedStatementQueryTest {

    @Test
    public void test(){

        String sql = "select order_id orderId, order_name as orderName, order_date orderDate from `order`;";
        List<Order> orderList = commonQuery(Order.class, sql);
        System.out.println(orderList);

        sql = "select id, name, birth, email from customers where id > ?";
        List<Customer> customerList = commonQuery(Customer.class, sql, 15);
        customerList.forEach(System.out::println); // 方法引用




    }


    /**
     * @Date: 2020/11/6 17:20
     * @Description: 返回不同表中多个记录
     *
     * @Param: [clazz 类, sql, args]
     * @return: list<T> 泛型，可以传任意类
     */
    public <T> List<T> commonQuery(Class<T> clazz, String sql, Object ...args){
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
                List<T> tList = new ArrayList<>();

                while(resultSet.next()){
                    // 这时不能直接通过new来获取实现类对象
                    // 那么就用反射
                    T t = clazz.newInstance();

                    // metaData.getColumnCount()：从结果集元数据中提取出字段个数
                    for (int i = 0; i < metaData.getColumnCount(); i++) {
                        // 从结果集中，提取字段的值
                        Object columnValue = resultSet.getObject(i + 1);// 由于不知道是什么类型，所以用Object

                        // 从结果集元数据中，提取字段的字段名，与值对应
                        // getColumnName()：获取列名 ---不推荐使用
                        // getColumnLabel()：获取别名，当sql语句没有别名时，这样获取的就是列名
//                        String columnName = metaData.getColumnName(i + 1);
                        String columnLabel = metaData.getColumnLabel(i + 1);

                        // 通过反射给创建的这个 t 对象属性赋值
                        // 获取当前的字段对应类中的属性镀锡
                        Field field = clazz.getDeclaredField(columnLabel);
                        // 防止该属性是私有属性导致无法set成功
                        field.setAccessible(true);
                        field.set(t, columnValue); // 给指定 t 对象set指定属性的值
                    }

                    // 完成一个 t 的属性填充，就向返回tList中添加
                    tList.add(t);
                }

                // 6. 关闭资源
                JDBCUtils.closeResource(conn, ps, resultSet);

                // 最后返回查询后封装的数据
                return tList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // 6. 关闭资源，这个地方也要关闭
            JDBCUtils.closeResource(conn, ps, resultSet);
        }

        return null; // 没有查询到数据返回null，一定要放在最后
    }


}
