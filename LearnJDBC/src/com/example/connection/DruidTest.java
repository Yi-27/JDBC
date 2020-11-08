package com.example.connection;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.DataSources;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-08 17:08
 * @Description:
 *      使用阿里的Druid数据库连接池技术
 *
 */
public class DruidTest {

    @Test
    public void testGetConnection() throws Exception {
        Properties pros = new Properties();

        // 反射获取类
        InputStream resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
        pros.load(resourceAsStream);

        // 创建数据库连接池
        DataSource dataSource = DruidDataSourceFactory.createDataSource(pros);

        // 获取连接
        Connection connection = dataSource.getConnection();
        System.out.println(connection);

        // 关闭资源
        connection.close();
        DataSources.destroy(dataSource);
    }

}
