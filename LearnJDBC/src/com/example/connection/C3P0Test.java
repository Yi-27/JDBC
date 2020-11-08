package com.example.connection;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import org.junit.Test;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-08 15:39
 * @Description:
 *      数据库连接池C3P0的测试
 *
 */
public class C3P0Test {

    // 方式一：硬编码方式（不推荐）
    @Test
    public void testGetConnection() throws PropertyVetoException, SQLException {

        // 数据连接池对象
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        // 设置驱动
        cpds.setDriverClass("com.mysql.jdbc.Driver");
        // 设置JDBC的url
        cpds.setJdbcUrl("jdbc:mysql://localhost:3306/jdbc_test?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true");
        // 设置用户名和密码
        cpds.setUser("root");
        cpds.setPassword("rootroot");


        // 通过设置相关参数，对数据库连接池进行管理
        // 设置初始时连接池中的连接数
        cpds.setInitialPoolSize(10);


        // 从数据库中获取连接
        Connection conn = cpds.getConnection();
        System.out.println(conn);

        // 关闭数据库连接（并不是真正关闭，只是把连接返还给数据库连接池
        conn.close();


//        cpds.close(); ???
        // 销毁数据库连接池
//        DataSources.destroy(cpds);
    }


    // 方式二：从文件中读取配置（可以是properties，也可以是xml文件
    @Test
    public void testGetConnection2() throws SQLException {

        // 获取数据库连接池 使用xml配置文件来读取配置信息
        ComboPooledDataSource cpds = new ComboPooledDataSource("helloc3p0");
        // 从数据库连接池中提取一个连接
        Connection conn = cpds.getConnection();
        System.out.println(conn);

        conn.close();
        DataSources.destroy(cpds);

    }

}
