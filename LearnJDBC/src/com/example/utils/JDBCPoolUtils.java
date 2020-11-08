package com.example.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-08 16:28
 * @Description:
 *      数据库连接池工具类
 */
public class JDBCPoolUtils {

    // c3p0数据库连接池创建一次就够
    private static ComboPooledDataSource cpds = new ComboPooledDataSource("helloc3p0");


    // dbcp数据库连接池可以使用静态代码块来获取
    private static DataSource sourceDBCP;
    static { // 这样就可以保证数据库连接池只创建一次
        try {
            Properties pros = new Properties();

            // 获取流的两种方式
            // 方式1：反射。先获取 系统类加载器，再加载配置文件
            InputStream resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");
            // 从该文件流中读取配置到Properties中
            pros.load(resourceAsStream);

            // 方式2：直接提取，这时是从项目根目录开始
//        FileInputStream fis = new FileInputStream("src/dbcp.properties");
//        pros.load(fis);

            // 根据配置文件来创建数据库连接池
            sourceDBCP = BasicDataSourceFactory.createDataSource(pros);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // Druid数据库连接池创建一个就够了
    private static DataSource sourceDruid;
    static {
        try {
            Properties pros = new Properties();

            // 反射获取类
            InputStream resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
            pros.load(resourceAsStream);

            // 创建数据库连接池
            sourceDruid = DruidDataSourceFactory.createDataSource(pros);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * @Date: 2020/11/8 17:02
     * @Description:
     *      使用c3p0数据库连接池获取连接
     *
     * @Param: []
     * @return: java.sql.Connection
     */        
    public static Connection getConnectionFromC3P0() throws SQLException {
        return cpds.getConnection();
    }

    /**
     * @Date: 2020/11/8 17:02
     * @Description: 
     *      使用dbcp连接池技术获取连接
     *
     * @Param: []
     * @return: java.sql.Connection
     */        
    public static Connection getConnectionFromDBCP() throws Exception {
        return sourceDBCP.getConnection();
    }


    public static Connection getConnectionFromDruid() throws SQLException {
        return sourceDruid.getConnection();
    }
}
