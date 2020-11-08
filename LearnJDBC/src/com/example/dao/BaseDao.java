package com.example.dao;

import com.example.utils.JDBCUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
 *  声明为abstract类，不能直接用来实例化
 */
public abstract class BaseDao<T> {

    /*
    该抽象类，加入泛型，使其能用更多情况下
    目的是为了该抽象类的实现类中调用下面的方法由于很明确是针对哪个bean对象来操作的，
    因此在方法中就可以省略Class<T> clazz
        即，原本对于Customers表的DaoImpl中
        调用下面的方法，需要指定Customer.class
        这是可以省略的，因为很明确是哪个类来调用了

     那么。如果能在该BaseDao中想办法获取到是针对哪个表哪个Bean对象来操作的，就好办了
     而 泛型 + 反射 就能很好的解决这个问题

     当然不省略也是可以的，并没有多大的影响
     */
    Class<T> clazz = null;
    {
        // 通过 实现类（当前抽象类的子类） 获取 其继承的当前抽象类 的 泛型
        Type genericSuperclass = this.getClass().getGenericSuperclass(); // 获取带泛型的父类
        System.out.println("genericSuperclass = " + genericSuperclass); // com.example.dao.BaseDao<com.example.bean.Customer>
        ParameterizedType paramType = (ParameterizedType) genericSuperclass; // 强转成带参数的泛型
        System.out.println("paramType = " + paramType); // com.example.dao.BaseDao<com.example.bean.Customer>

        // 获取父类中泛型中的参数 可能是多个，但对于当前情况是一个
        Type[] typeArguments = paramType.getActualTypeArguments();
        System.out.println("typeArguments[0] = " + typeArguments[0]); // class com.example.bean.Customer

        // 泛型的第一个参数，比如获取到lCustomer，在强转成类对象
        // 这里强转的原因：从上面的打印可以看到 typeArguments[0]的确是个类，但是由于他现在是Type对象，需要转成类对象后续才能使用
        // 另外，Class类 实现了 Type 接口，这也是多态的使用情况
        clazz = (Class<T>) typeArguments[0];
        System.out.println("clazz = " + clazz); // class com.example.bean.Customer
    }


    /**
     * @Date: 2020/11/7 19:58
     * @Description:
     *  通用的查询操作，用于返回数据表中的一些数据 这里考虑到事务
     *  这里不自己获取连接，用传过来的连接，并且不再这里关闭，让上层自己关
     *
     * @Param: [conn, clazz, sql, args]
     * @return: java.util.List<T>
     */
    public List<T> commonQuery(Connection conn, String sql, Object ...args){
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            // 2. 预编译SQL语句
            ps = conn.prepareStatement(sql);
            // 3. 向占位符填入数据 没有占位符这里就不用考虑了
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


    public <E> E getValue(Connection conn, String sql, Object ...args) {
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = conn.prepareStatement(sql);
            // 填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i+1, args[i]);
            }

            // 执行
            resultSet = ps.executeQuery();
            if (resultSet.next()){
                return (E) resultSet.getObject(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            JDBCUtils.closeResource(null, ps, resultSet);
        }

        return null;
    }

}
