#  Java中的数据存储技术

- 在Java中，数据库存取技术可分为如下几类：
    - **JDBC**直接访问数据库
    - JDO (Java Data Object )技术

    - **第三方O/R工具**，如Hibernate, Mybatis 等

- JDBC是java访问数据库的基石，JDO、Hibernate、MyBatis等只是更好的封装了JDBC。



# JDBC介绍

- JDBC(Java Database Connectivity)是一个**独立于特定数据库管理系统、通用的SQL数据库存取和操作的公共接口**（一组API），定义了用来访问数据库的标准Java类库，（**java.sql,javax.sql**）使用这些类库可以以一种**标准**的方法、方便地访问数据库资源。
- JDBC为访问不同的数据库提供了一种**统一的途径**，为开发者屏蔽了一些细节问题。
- JDBC的目标是使Java程序员使用JDBC可以连接任何**提供了JDBC驱动程序**的数据库系统，这样就使得程序员无需对特定的数据库系统的特点有过多的了解，从而大大简化和加快了开发过程。



###  JDBC体系结构

- JDBC接口（API）包括两个层次：
    - **面向应用的API**：Java API，抽象接口，供应用程序开发人员使用（连接数据库，执行SQL语句，获得结果）。
    - **面向数据库的API**：Java Driver API，供开发商开发数据库驱动程序用。



# JDBC获取数据库连接方法

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

方式五：

```java
// 方式五：final版，将数据库连接需要的4个基本信息声明在配置文件中，通过读取配置文件的方式，获取连接
/*
这样做的好处？
    1. 实现了数据与代码的分离，实现了解耦
    2. 如果需要修改配置信息，可以避免程序重新打包
 */
@Test
public void testConnection5() throws Exception {
    // 1. 读取配置文件的信息
    // 使用类的加载器，这里的用的是系统类加载器，因为自定义类都是它加载的
    // getResourceAsStream()的默认识别路径就是src目录下，作用获取类路径下指定文件的输入流对象
    InputStream resourceAsStream = ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties");

    // 2. 转载配置信息
    Properties properties = new Properties();
    properties.load(resourceAsStream); // 加载配置文件信息

    // 3. 提取字段信息
    String user = properties.getProperty("user");
    String password = properties.getProperty("password");
    String url = properties.getProperty("url");
    String driverClass = properties.getProperty("driverClass");

    // 4. 获取Driver实现类对象
    Class.forName(driverClass);

    // 5. 获取连接
    Connection connection = DriverManager.getConnection(url, user, password);
    System.out.println(connection);

    // 6. 关闭连接
    connection.close();
}
```

JDBC中URL的编写方式：

- jdbc:mysql://localhost:3306/atguigu**?useUnicode=true&characterEncoding=utf8**（如果JDBC程序与服务器端的字符集不一致，会导致乱码，那么可以通过参数指定服务器端的字符集）
- jdbc:mysql://localhost:3306/atguigu?user=root&password=123456



# 使用PreparedStatement实现CRUD操作

- 数据库连接被用于向数据库服务器发送命令和 SQL 语句，并接受数据库服务器返回的结果。其实一个数据库连接就是一个Socket连接。

- 在 java.sql 包中有 3 个接口分别定义了对数据库的调用的不同方式：
    - Statement：用于执行静态 SQL 语句并返回它所生成结果的对象。 
    - PrepatedStatement：SQL 语句被预编译并存储在此对象中，可以使用此对象多次高效地执行该语句。
    - CallableStatement：用于执行 SQL 存储过程



### 使用Statement操作数据表的弊端

- 通过调用 Connection 对象的 createStatement() 方法创建该对象。该对象用于执行静态的 SQL 语句，并且返回执行结果。

- Statement 接口中定义了下列方法用于执行 SQL 语句：

    ```sql
    int excuteUpdate(String sql)：执行更新操作INSERT、UPDATE、DELETE
    ResultSet executeQuery(String sql)：执行查询操作SELECT
    ```

- 但是使用Statement操作数据表存在弊端：

    - **问题一：存在拼串操作，繁琐**
    - **问题二：存在SQL注入问题**

- SQL 注入是利用某些系统没有对用户输入的数据进行充分的检查，而在用户输入数据中注入非法的 SQL 语句段或命令(如：SELECT user,password FROM user_table WHERE USER = '1' or ' AND PASSWORD = '='1' or '1' = '1';) ，从而利用系统的 SQL 引擎完成恶意行为的做法。

- 对于 Java 而言，要防范 SQL 注入，只要用 PreparedStatement(从Statement扩展而来) 取代 Statement 就可以了。



### PreparedStatement介绍

- 可以通过调用 Connection 对象的 **preparedStatement(String sql)** 方法获取 PreparedStatement 对象

- **PreparedStatement 接口是 Statement 的子接口，它表示一条预编译过的 SQL 语句**

- PreparedStatement 对象所代表的 SQL 语句中的参数用问号(?)来表示，调用 PreparedStatement 对象的 setXxx() 方法来设置这些参数. setXxx() 方法有两个参数，第一个参数是要设置的 SQL 语句中的参数的索引(从 1 开始)，第二个是设置的 SQL 语句中的参数的值

####  PreparedStatement vs Statement

- 代码的可读性和可维护性。

- **PreparedStatement 能最大可能提高性能：**
    - DBServer会对**预编译**语句提供性能优化。因为预编译语句有可能被重复调用，所以<u>语句在被DBServer的编译器编译后的执行代码被缓存下来，那么下次调用时只要是相同的预编译语句就不需要编译，只要将参数直接传入编译过的语句执行代码中就会得到执行。</u>
    - 在statement语句中,即使是相同操作但因为数据内容不一样,所以整个语句本身不能匹配,没有缓存语句的意义.事实是没有数据库会对普通语句编译后的执行代码缓存。这样<u>每执行一次都要对传入的语句编译一次。</u>
    - (语法检查，语义检查，翻译成二进制命令，缓存)

- PreparedStatement 可以防止 SQL 注入 

+ PreparedStatement可以操作Blob的数据，而Statement做不到
+ PreparedStatement可以实现更高效的批量操作



# ORM编程思想

object relational mapping

+ 一个数据表对应一个java类
+ 表中的一条记录对应java类的一个对象
+ 表中的一个字段对应java类的一个属性



## 针对不同表的通用的数据查询

```java
/**
     * @Date: 2020/11/6 17:20
     * @Description: 返回不同表中多个记录
     *
     * @Param: [clazz 类, sql, args]
     * @return: list<T> 泛型，可以传任意类
     */
    public <T> List<T> commonQuery(Class<T> clazz, String sql, Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            // 1. 获取连接
            conn = JDBCUtils.getConnection();
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

                // 6. 关闭资源
                JDBCUtils.closeResource(conn, ps, resultSet);

                // 最后返回查询后封装的数据
                return tList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // 6. 关闭资源，这个地方也要关闭
            JDBCUtils.closeResource(conn, ps, resultSet);
        }

        return null; // 没有查询到数据返回null，一定要放在最后
    }
```



#### 注：boolean execute()：

+ 如果执行的是**查询操作**，有返回结果，则此方法**返回true**
    + ResultSet executeQuery()才会得到返回的结果
+ 如果执行的是**增、删、改操作**，没有返回结果，则此方法**返回false**

#### int executeUpdate()：

+ 增、删、改操作也可以使用这个方法
+ 该方法返回值为执行该方法的sql语句编译对象（preparedStatement）影响了多少条数据，没有影响数据返回0
+ 另外要注意区分 int executeUpdate(String sql) 该方法的重载方法（有多个），这些重载方法是给statement使用的，而不是preparedStatement



### 操作BLOB类型字段

photo（图片）在数据库中的数据类型是mediumblob

还有好几种Blob类型的数据



### MySQL BLOB类型

- MySQL中，BLOB是一个二进制大型对象，是一个可以存储大量数据的容器，它能容纳不同大小的数据。
- 插入BLOB类型的数据必须使用PreparedStatement，因为BLOB类型的数据无法使用字符串拼接写的。

- MySQL的四种BLOB类型(除了在存储的最大信息量上不同外，他们是等同的)

![1555581069798](D:/BaiduNetdiskDownload/恋词5500/尚硅谷—宋红康-JDBC/JDBC/1-课件/课件-md/尚硅谷_宋红康_JDBC.assets/1555581069798.png)

- 实际使用中根据需要存入的数据大小定义不同的BLOB类型。
- 需要注意的是：如果存储的文件过大，数据库的性能会下降。
- 如果在指定了相关的Blob类型以后，还报错：xxx too large，那么在mysql的安装目录下，找my.ini文件加上如下的配置参数： **max_allowed_packet=16M**。同时注意：修改了my.ini文件之后，需要重新启动mysql服务。
    - 默认是1M



## 批量插入

### 批量执行SQL语句

当需要成批插入或者更新记录时，可以采用Java的批量**更新**机制，这一机制允许多条语句一次性提交给数据库批量处理。通常情况下比单独提交处理更有效率

JDBC的批量处理语句包括下面三个方法：

- **addBatch(String)：添加需要批量处理的SQL语句或是参数；**
- **executeBatch()：执行批量处理语句；**
- **clearBatch():清空缓存的数据**

通常我们会遇到两种批量执行SQL语句的情况：

- 多条SQL语句的批量处理；
- 一个SQL语句的批量传参；



**删除和更新操作天然具备批量操作**





在JDBC的url字段上添加 `rewriteBatchedStatements=true` 即可开启批处理模式

```java
// 方式四：手动设置事务
@Test
public void test3(){

    Connection conn = null;
    PreparedStatement ps = null;
    try {
        long start = System.currentTimeMillis();

        conn = JDBCUtils.getConnection();

        // 设置不允许自动提交数据
        conn.setAutoCommit(false);

        String sql = "insert into goods(`NAME`) values(?)"; // 这里一定要注意！！！不能在最后加 分号; ，这样会导致批量处理失败
        ps = conn.prepareStatement(sql);

        // 批量插入
        for (int i = 1; i <= 1000000; i++) {
            ps.setObject(1, "name_"+i);

            // 1. 攒 "sql"
            ps.addBatch();

            if (i % 500 == 0) { // 每500执行一次 控制这个数也可以提高点速度
                // 2. 执行batch
                ps.executeBatch();

                // 3. 清空已执行的batch
                ps.clearBatch();

            }
        }
        // 提交数据
        conn.commit();

        long end = System.currentTimeMillis();

        System.out.println("花费的时间为：" + (end - start)); // 花费的时间为：3954
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // 关闭资源
        JDBCUtils.closeResource(conn, ps);
    }
}
```





## 数据库事务

### 数据库事务介绍

- **事务：一组逻辑操作单元,使数据从一种状态变换到另一种状态。**

- **事务处理（事务操作）：**保证所有事务都作为一个工作单元来执行，即使出现了故障，都不能改变这种执行方式。当在一个事务中执行多个操作时，要么所有的事务都**被提交(commit)**，那么这些修改就永久地保存下来；要么数据库管理系统将放弃所作的所有修改，整个事务**回滚(rollback)**到最初状态。

- 为确保数据库中数据的**一致性**，数据的操纵应当是离散的成组的逻辑单元：当它全部完成时，数据的一致性可以保持，而当这个单元中的一部分操作失败，整个事务应全部视为错误，所有从起始点以后的操作应全部回退到开始状态。 

###  JDBC事务处理

- 数据一旦提交，就不可回滚。

- 数据什么时候意味着提交？

    - **当一个连接对象被创建时，默认情况下是自动提交事务**：每次执行一个 SQL 语句时，如果执行成功，就会向数据库自动提交，而不能回滚。
        - DDL操作一旦执行，都会自动提交
            - set autocommit = false对DDL操作失效
        - DML默认情况下，一旦执行就会自动提交
            - 可以通过set autocommit = false取消DML操作的自动提交
    - **关闭数据库连接，数据就会自动的提交。**如果多个操作，每个操作使用的是自己单独的连接，则无法保证事务。即同一个事务的多个操作必须在同一个连接下。

- **JDBC程序中为了让多个 SQL 语句作为一个事务执行：**

    - 调用 Connection 对象的 **setAutoCommit(false);** 以取消自动提交事务
    - 在所有的 SQL 语句都成功执行后，调用 **commit();** 方法提交事务
    - 在出现异常时，调用 **rollback();** 方法回滚事务

    > 若此时 Connection 没有被关闭，还可能被重复使用，则需要恢复其自动提交状态 setAutoCommit(true)。**尤其是在使用数据库连接池技术时，执行close()方法前，建议恢复自动提交状态。**



### 事务的ACID属性    

1. **原子性（Atomicity）**
    原子性是指事务是一个不可分割的工作单位，事务中的操作要么都发生，要么都不发生。 

2. **一致性（Consistency）**
    事务必须使数据库从一个一致性状态变换到另外一个一致性状态。

3. **隔离性（Isolation）**
    事务的隔离性是指一个事务的执行不能被其他事务干扰，即一个事务内部的操作及使用的数据对并发的其他事务是隔离的，并发执行的各个事务之间不能互相干扰。

4. **持久性（Durability）**
    持久性是指一个事务一旦被提交，它对数据库中数据的改变就是永久性的，接下来的其他操作和数据库故障不应该对其有任何影响。

#### 数据库的并发问题

- 对于同时运行的多个事务, 当这些事务访问数据库中相同的数据时, 如果没有采取必要的隔离机制, 就会导致各种并发问题:
    - **脏读**: 对于两个事务 T1, T2, T1 读取了已经被 T2 更新但还**没有被提交**的字段。之后, 若 T2 回滚, T1读取的内容就是临时且无效的。
    - **不可重复读**: 对于两个事务T1, T2, T1 读取了一个字段, 然后 T2 **更新**了该字段。之后, T1再次读取同一个字段, 值就不同了。
    - **幻读**: 对于两个事务T1, T2, T1 从一个表中读取了一个字段, 然后 T2 在该表中**插入**了一些新的行。之后, 如果 T1 再次读取同一个表, 就会多出几行。

- **数据库事务的隔离性**: 数据库系统必须具有隔离并发运行各个事务的能力, 使它们不会相互影响, 避免各种并发问题。

- 一个事务与其他事务隔离的程度称为隔离级别。数据库规定了多种事务隔离级别, 不同隔离级别对应不同的干扰程度, **隔离级别越高, 数据一致性就越好, 但并发性越弱。**



####  四种隔离级别

- 数据库提供的4种事务隔离级别：

    ![1555586275271](D:/BaiduNetdiskDownload/恋词5500/尚硅谷—宋红康-JDBC/JDBC/1-课件/课件-md/尚硅谷_宋红康_JDBC.assets/1555586275271.png)

- Oracle 支持的 2 种事务隔离级别：**READ COMMITED**, SERIALIZABLE。 Oracle 默认的事务隔离级别为: **READ COMMITED** 。


- Mysql 支持 4 种事务隔离级别。Mysql 默认的事务隔离级别为: **REPEATABLE READ。**



#### 在MySql中设置隔离级别

- 每启动一个 mysql 程序, 就会获得一个单独的数据库连接. 每个数据库连接都有一个全局变量 @@tx_isolation, 表示当前的事务隔离级别。

- 查看当前的隔离级别: 

    ```mysql
    SELECT @@tx_isolation;
    ```

- 设置当前 mySQL 连接的隔离级别:  

    ```mysql
    set  transaction isolation level read committed;
    ```

- 设置数据库系统的全局的隔离级别:

    ```mysql
    set global transaction isolation level read committed;
    ```

- 补充操作：

    - 创建mysql数据库用户：

        ```mysql
        create user tom identified by 'abc123';
        ```

    - 授予权限

        ```mysql
        #授予通过网络方式登录的tom用户，对所有库所有表的全部权限，密码设为abc123.
        grant all privileges on *.* to tom@'%'  identified by 'abc123'; 
        
         #给tom用户使用本地命令行方式，授予atguigudb这个库下的所有表的插删改查的权限。
        grant select,insert,delete,update on atguigudb.* to tom@localhost identified by 'abc123'; 
        
        ```

        

在JDBC中事务的隔离级别对应的int值为

+ TRANSCATION_NONE（无隔离）：0
+ TRANSCATION_READ_UNCOMMITTED（读未提交数据）：1
+ TRANSCATION_READ_COMMITTED（读已提交数据）：2
+ TRANSCATION_REPEATABLE_READ（可重复读）：4
+ TRANSCATION_SERIALIZABLE（串行化）：8

这其实对应二级制`0000`、`0001`、`0010`、`0100`、`1000`





## DAO及相关实现类

- DAO：**Data Access Object**访问数据信息的类和接口，包括了对数据的CRUD（Create、Retrival、Update、Delete），而不包含任何业务相关的信息。有时也称作：BaseDAO
- 作用：为了实现功能的模块化，更有利于代码的维护和升级。