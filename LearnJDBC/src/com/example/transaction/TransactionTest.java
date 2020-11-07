package com.example.transaction;

import com.example.bean.User;
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
 * @create: 2020-11-07 16:19
 * @Description: 数据库事务管理
 *      事务：一组逻辑操作单元，是数据从一种状态变换到另一种状态
 *          > 一组逻辑操作单元，一个或多个DML操作
 */
public class TransactionTest {


    // 测试查询
    @Test
    public void testTransactionSelect() throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        Connection conn = JDBCUtils.getConnection();
        System.out.println("conn = " + conn);

        // 关闭当前事务的自动提交
        conn.setAutoCommit(false);

        // 修改数据库事务隔离级别 只是设置当前事务，并不会影响到全局
//        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); // 2 读已提交

        // 获取数据库事务隔离级别
        System.out.println("数据库的事务隔离级别为： " + conn.getTransactionIsolation()); // 4 TRANSACTION_REPEATABLE_READ 可重复读 只要别的事务没提交就每次读到的都和第一次一样

        String sql = "select user, password, balance from user_table where user = ? ";
        // 通过通用的查询方法来获取查询内容并封装进对象中
        List<User> cc = commonQuery(conn, User.class, sql, "CC");
        System.out.println(cc); // [User{user='CC', password='abcd', balance=3000}]
        System.out.println(conn.isClosed());
        Thread.sleep(10000);
        System.out.println(conn);
        System.out.println(conn.isClosed());

        List<User> cc2 = commonQuery(conn, User.class, sql, "CC"); // 由于隔离级别为 可重复读 ，因此在没commit之前读到的值都是一样的
        System.out.println(cc2); // [User{user='CC', password='abcd', balance=3000}]

        conn.commit();

        List<User> cc3 = commonQuery(conn, User.class, sql, "CC"); // commit后再查就可以看到下面更新的值了
        System.out.println(cc3); // [User{user='CC', password='abcd', balance=5000}]
    }


    // 测试更新
    @Test
    public void testTransactionUpdate() throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        Connection conn = JDBCUtils.getConnection();
        System.out.println("conn = " + conn);

        // 获取数据库事务隔离级别
        System.out.println("数据库的事务隔离级别为： " + conn.getTransactionIsolation());

        // 关闭当前事务的自动提交
        conn.setAutoCommit(false);

        String sql = "update user_table set balance = ? where user = ?";
        commonUpdate(conn, sql, 5000, "CC");

//        Thread.sleep(15000);
        conn.commit();
        System.out.println("修改成功");

        JDBCUtils.closeResource(conn, null);
        System.out.println(conn.isClosed());
    }




    /**
     * @Date: 2020/11/7 19:58
     * @Description:
     *  通用的查询操作，用于返回数据表中的一些数据 这里考虑到事务
     *  这里不自己获取连接，用传过来的连接，并且不再这里关闭，让上层自己关
     *
     * @Param: [conn, clazz, sql, args]
     * @return: java.util.List<T>
     */
    public <T> List<T> commonQuery(Connection conn, Class<T> clazz, String sql, Object ...args){
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            // 1. 获取连接
//            conn = JDBCUtils.getConnection(); ！！！ 这个地方再写事务隔离就没意义了
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

                // 最后返回查询后封装的数据
                return tList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // 6. 关闭资源，这个地方也要关闭
            JDBCUtils.closeResource(null, ps, resultSet);
        }

        return null; // 没有查询到数据返回null，一定要放在最后
    }





    // 考虑事务的转账操作
    @Test
    public void testUpdateWithTx(){
        Connection conn = null;
        try {
            // 获取连接
            conn = JDBCUtils.getConnection();

            // 1. 取消数据的自动提交功能，开启事务
            System.out.println("conn.getAutoCommit() = " + conn.getAutoCommit());
            conn.setAutoCommit(false);


            String sql1 = "update user_table set balance = balance - 100 where user = ?";
            commonUpdate(conn, sql1, "AA"); // AA 账户 -100

            // 模拟网络异常
            System.out.println(10 / 0);

            String sql2 = "update user_table set balance = balance + 100 where user = ?";
            commonUpdate(conn, sql2, "BB"); // BB 账户 +100

            System.out.println("转账成功！");

            // 2. 提交数据
            conn.commit();
        } catch (Exception e) {
            // 3. 回滚操作
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            // 恢复自动提交
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // 关闭资源
            // 由于这里两个操作处于同一事务中，因此用到是同一个连接
            JDBCUtils.closeResource(conn, null);
        }
    }


    /**
     * @Date: 2020/11/7 17:12
     * @Description:
     *     考虑数据库事务
     *     通用的增删改操作
     *
     * @Param: [conn, sql, objects]
     * @return: int
     */
    public int commonUpdate(Connection conn, String sql, Object ...args){
        // 这里直接传过来了连接，因此就不需要手动获取连接了
        PreparedStatement ps = null;

        try {
            // 预编译SQL语句
            ps = conn.prepareStatement(sql);
            // 填占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i+1, args[i]);
            }

            // 执行操作并返回
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            JDBCUtils.closeResource(null, ps); // 这里由于提供了conn所以不再这里关，交给上面一层关，关闭连接内部有非空判断
        }

        return 0;
    }
}
