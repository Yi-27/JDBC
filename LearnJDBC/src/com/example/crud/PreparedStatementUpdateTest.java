package com.example.crud;

import com.example.utils.JDBCUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author Yi-27
 * @create 2020-11-06 9:06
 */
public class PreparedStatementUpdateTest {

    // 增删改
    @Test
    public void insertTest(){
        InputStream is = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // 1. 从配置文件中加载信息
            is = PreparedStatementUpdateTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
            Properties properties = new Properties();
            properties.load(is);

            String user = properties.getProperty("user");
            String password = properties.getProperty("password");
            String url = properties.getProperty("url");
            String driverClass = properties.getProperty("driverClass");

            // 2. 获取Diver实现类及获取连接
            Class.forName(driverClass);
            connection = DriverManager.getConnection(url, user, password);

            // 3. 需要执行插入操作的SQL语句
            // ? 是占位符
            String sql = "insert into customers(name, email, birth) values(?, ?, ?)";

            // 4. 获取SQL预编译平台对象
            preparedStatement = connection.prepareStatement(sql);

            // 5. 向占位符放入数据，占位符的索引是从1开始的
            preparedStatement.setString(1, "新垣结衣");
            preparedStatement.setString(2, "gakki@qq.com");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 时间格式化
            Date date = sdf.parse("2020-11-6"); // 按时间格式化对象的形式创建事件对象
            preparedStatement.setDate(3, new java.sql.Date(date.getTime())); // 注意这个地方必须要用sql.Date而不是util.Date

            // 6. 执行操作
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 7. 关闭资源
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void updateTest(){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            // 1. 获取数据库的连接
            connection = JDBCUtils.getConnection();

            // 2. 预编译sql语句，返回PreparedStatement的实例
            String sql = "update customers set name = ? where id = ?";
            preparedStatement = connection.prepareStatement(sql);

            // 3. 填充占位符
            preparedStatement.setObject(1, "莫扎特");
            preparedStatement.setObject(2, 18);

            // 4. 执行sql语句
            preparedStatement.execute();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 5. 资源的关闭
            JDBCUtils.closeResource(connection, preparedStatement);
        }
    }

    /**
     * @Date: 2020/11/6 11:02
     * @Description: 通用的JDBC增删改操作
     * 其中args是可变形参
     *
     * @Param: [sql, args]
     * @return: void
     */
    public void commonExecute(String sql, Object ...args){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            // 1. 获取数据库的连接
            connection = JDBCUtils.getConnection();

            // 2. 预编译sql语句，返回PreparedStatement的实例
            preparedStatement = connection.prepareStatement(sql);

            // 3. 填充占位符
            for (int i = 0; i < args.length; i++) {
                // 这里注意占位符的索引是从1开始，而数组的索引是从0开始的
                preparedStatement.setObject(i+1, args[i]);
            }

            // 4. 执行sql语句
            preparedStatement.execute();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 5. 资源的关闭
            JDBCUtils.closeResource(connection, preparedStatement);
        }
    }

    @Test
    public void commonTest(){

       String sql = "delete from customers where id = ?";
       commonExecute(sql, 3);

       String sql2 = "update `order` set order_name = ? where order_id = ?";
       commonExecute(sql2, "DD", "2");
    }
}
