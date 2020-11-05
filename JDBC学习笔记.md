###  Java中的数据存储技术

- 在Java中，数据库存取技术可分为如下几类：
    - **JDBC**直接访问数据库
    - JDO (Java Data Object )技术

    - **第三方O/R工具**，如Hibernate, Mybatis 等

- JDBC是java访问数据库的基石，JDO、Hibernate、MyBatis等只是更好的封装了JDBC。



### JDBC介绍

- JDBC(Java Database Connectivity)是一个**独立于特定数据库管理系统、通用的SQL数据库存取和操作的公共接口**（一组API），定义了用来访问数据库的标准Java类库，（**java.sql,javax.sql**）使用这些类库可以以一种**标准**的方法、方便地访问数据库资源。
- JDBC为访问不同的数据库提供了一种**统一的途径**，为开发者屏蔽了一些细节问题。
- JDBC的目标是使Java程序员使用JDBC可以连接任何**提供了JDBC驱动程序**的数据库系统，这样就使得程序员无需对特定的数据库系统的特点有过多的了解，从而大大简化和加快了开发过程。



###  JDBC体系结构

- JDBC接口（API）包括两个层次：
    - **面向应用的API**：Java API，抽象接口，供应用程序开发人员使用（连接数据库，执行SQL语句，获得结果）。
    - **面向数据库的API**：Java Driver API，供开发商开发数据库驱动程序用。



### JDBC获取数据库连接方法

方式一：

```java
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
```

方式二：对方式一的迭代

```java
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
}
```

方式三：

```java
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
```

方式四：

```java
// 方式四：优化方式三
@Test
public void testConnection4() throws Exception {
    // 1. 提供三个连接的基本信息 尽量不用硬编码这些信息
    String url = "jdbc:mysql://localhost:3306/jdbc_test";
    String user = "root";
    String password = "rootroot";

    // 2. 获取Driver实现类的对象
    Class.forName("com.mysql.jdbc.Driver"); // 这个也可以省，但是不要省

    // 3. 获取连接
    Connection connection = DriverManager.getConnection(url, user, password);
    System.out.println(connection);

    // 关闭连接
    connection.close();
}
```

