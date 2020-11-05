package com.example.connection;

import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Yi-27
 * @create 2020-11-05 21:19
 */
public class ConnectionTest {

    @Test
    public void testConnection1() throws SQLException {

        // 加载驱动
        Driver driver = new com.mysql.jdbc.Driver();
        // 连接数据库的链接
        // url: jdbc:mysql://localhost:3306/test
        // jdbc为主协议，mysql为子协议，
        // localhost为ip地址
        // 3306为mysql默认的端口号
        // jdbc_test为数据库库名
        String url = "jdbc:mysql://localhost:3306/jdbc_test";
        // 就用户名和密码封装在Properties中
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "rootroot");
        System.out.println(info);
        // 建立连接
        Connection connect = driver.connect(url, info);
        System.out.println(connect);
    }

    // 方式二：对方式一的迭代，在如下的程序中不出现第三方的api，使得程序具有更好的可移植性
    @Test
    public void testConnection2() throws Exception {

        // 1. 获取Driver的实现类对象，使用反射
        Class clazz = Class.forName("com.mysql.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        // 2. 提供连接的数据库
        String url = "jdbc:mysql://localhost:3306/jdbc_test";
        // 3. 将用户名和密码封装在Properties中
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "rootroot");
        System.out.println(info);
        // 4. 建立连接
        Connection connect = driver.connect(url, info);
        System.out.println(connect);
        connect.close();
    }

    // 方式三：使用DriverManager替换Driver
    @Test
    public void testConnection3() throws Exception {
        // 1. 获取Driver实现类对象
        Class clazz = Class.forName("com.mysql.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        // 2. 提供三个连接的基本信息
        String url = "jdbc:mysql://localhost:3306/jdbc_test";
        String user = "root";
        String password = "rootroot";

        // 注册驱动
        DriverManager.registerDriver(driver); // 这部在Java1.8不远古的版本中都可以省略了，在getConnection()中封装了

        // 建立连接
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);

        // 关闭连接
        connection.close();
    }

    // 方式四：优化方式三
    @Test
    public void testConnection4() throws Exception {
        // 1. 提供三个连接的基本信息
        String url = "jdbc:mysql://localhost:3306/jdbc_test";
        String user = "root";
        String password = "rootroot";

        // 2. 获取Driver实现类的对象
        Class.forName("com.mysql.jdbc.Driver");

        // 3. 获取连接
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);

        // 关闭连接
        connection.close();
    }

    // 方式五：final版，将数据库连接需要的4个基本信息声明在配置文件中，通过读取配置文件的方式，获取连接
    @Test
    public void testConnection5() throws Exception {
        // 1. 读取配置文件的信息
        // 使用类的加载器，这里的用的是系统类加载器，因为自定义类都是它加载的
        // getResourceAsStream()的默认识别路径就是src目录下，作用获取类路径下指定文件的输入流对象
        InputStream resourceAsStream = ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties");

        Properties properties = new Properties();
        properties.load(resourceAsStream); // 加载配置文件信息



    }
}
