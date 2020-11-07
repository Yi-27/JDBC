package com.example.dao;

import com.example.utils.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-07 22:21
 * @Description:
 *  通用的基础Dao（Data Access Object）
 *  封装了针对数据表的通用操作
 */
public class BaseDao {



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


    /**
     * @Date: 2020/11/7 17:12
     * @Description:
     *     考虑数据库事务
     *     通用的增删改操作，统一称为update
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


    public void getValue(Connection conn, String sql, Object ...args) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        // 填充占位符
        for (int i = 0; i < args.length; i++) {
            ps.setObject(i+1, args[i]);
        }

        // 执行
        ResultSet resultSet = ps.executeQuery();
//        if ()

    }

}
