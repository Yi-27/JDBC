package com.example.connection;

import com.mchange.v2.c3p0.DataSources;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-08 16:41
 * @Description:
 *      使用DBCP连接池技术
 */
public class DBCPTest {

    // 方式一：硬编码（不推荐）
    @Test
    public void testGetConnection() throws SQLException {

        // 创建了DBCP的数据库连接池
        BasicDataSource source = new BasicDataSource();

        // 设置基本信息
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setUrl("jdbc:mysql://localhost:3306/jdbc_test?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true");
        source.setUsername("root");
        source.setPassword("rootroot");

        // 还可以设置其他涉及数据库连接池管理的相关属性
        source.setInitialSize(10); // 初始化的连接数
        source.setMaxActive(10); // 最大连接数
        // ...

        Connection conn = source.getConnection();
        System.out.println(conn);

        conn.close();
        source.close();
    }

    // 方式二：使用配置文件
    @Test
    public void testGetConnection2() throws Exception {

        Properties pros = new Properties();

        // 获取流的两种方式
        // 方式1：反射。先获取 系统类加载器，再加载配置文件
//        InputStream resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");
//        // 从该文件流中读取配置到Properties中
//        pros.load(resourceAsStream);

        // 方式2：直接提取，这时是从项目根目录开始
        FileInputStream fis = new FileInputStream("src/dbcp.properties");
        pros.load(fis);

        // 根据配置文件来创建数据库连接池
        DataSource source = BasicDataSourceFactory.createDataSource(pros);

        // 获取连接
        Connection conn = source.getConnection();
        System.out.println(conn);

        conn.close();
        DataSources.destroy(source);
    }
}
