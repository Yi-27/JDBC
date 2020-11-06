package com.example.utils;

import com.example.crud.PreparedStatementUpdateTest;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-06 10:18
 * @Description: 操作数据库的工具类
 */
public class JDBCUtils {

    /**
     * @Date: 2020/11/6 10:23
     * @Description: 获取数据库连接操作
     *
     * @Param: []
     * @return: java.sql.Connection
     */
    public static Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
        // 1. 从配置文件中加载信息
        InputStream is = PreparedStatementUpdateTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(is);
        if (is != null)
            is.close(); // 关闭流

        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String url = properties.getProperty("url");
        String driverClass = properties.getProperty("driverClass");

        // 2. 获取Diver实现类及获取连接
        Class.forName(driverClass);
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * @Date: 2020/11/6 10:27
     * @Description: 关闭资源，这里不用preparedStatement是因为用Statement是多态的用法
     *
     * @Param: [is, conn, statement]
     * @return: void
     */        
    public static void closeResource(Connection conn, Statement statement){
        // 关闭资源
        try {
            if (statement != null)
                statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Date: 2020/11/6 12:29
     * @Description: 重载关闭资源方法，用于查询操作
     *
     * @Param: [conn, statement, resultSet]
     * @return: void
     */        
    public static void closeResource(Connection conn, Statement statement, ResultSet resultSet) {
        // 关闭资源
        closeResource(conn, statement);
        try {
            if (resultSet != null)
                resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
