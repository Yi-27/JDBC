package dbutils;

import com.example.bean.Customer;
import com.example.utils.JDBCPoolUtils;
import com.example.utils.JDBCUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.*;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-08 19:28
 * @Description:
 *      第三方开源 dbUtils 使用
 *      其实跟我们自己写的 JDBCUtils 差不多
 *      就是健壮性比我们的强一点而已
 *      思路是差不多的
 *
 */
public class QueryRunnerTest {

    // 测试插入， 删和改也相似
    @Test
    public void testInsert(){
        Connection conn = null;
        try {
            QueryRunner queryRunner = new QueryRunner();
            // 从连接池获取一个连接
            conn = JDBCPoolUtils.getConnectionFromDruid();

            String sql = "insert into customers(name, email, birth) values(?, ?, ?);";
            // 执行插入操作
            int insertCount = queryRunner.update(conn, sql, "西野司", "xiyesi@qq.com", "1998-06-04");
            System.out.println("插入了 " + insertCount + " 条数据");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    /*
    测试查询
    BeanHandler:是ResultSetHandler接口的实现类，，在查询时需要提供
     */
    @Test
    public void testQuery1(){
        Connection conn = null;
        try {
            QueryRunner queryRunner = new QueryRunner();
            // 从连接池获取一个连接
            conn = JDBCPoolUtils.getConnectionFromDruid();
            String sql = "select id, name, birth, email from customers where id = ?;";

            // 获取封装表中一条记录的对象
            BeanHandler<Customer> handler = new BeanHandler<>(Customer.class);
            Customer customer = queryRunner.query(conn, sql, handler, 20);
            System.out.println(customer);


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }


    /*
    测试查询
    BeanListHandler:用于封装多条记录
     */
    @Test
    public void testQuery2(){
        Connection conn = null;
        try {
            QueryRunner queryRunner = new QueryRunner();
            // 从连接池获取一个连接
            conn = JDBCPoolUtils.getConnectionFromDruid();
            String sql = "select id, name, birth, email from customers where id > ?;";

            // 获取封装表中多条记录的对象
            BeanListHandler<Customer> handler = new BeanListHandler<>(Customer.class);
            List<Customer> customerList = queryRunner.query(conn, sql, handler, 19);
            customerList.forEach(System.out::println);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    /*
    测试查询
    MapHandler:用于以Map的形式封装一条记录
     */
    @Test
    public void testQuery3(){
        Connection conn = null;
        try {
            QueryRunner queryRunner = new QueryRunner();
            // 从连接池获取一个连接
            conn = JDBCPoolUtils.getConnectionFromDruid();
            String sql = "select id, name, birth, email from customers where id = ?;";

            // 获取封装表中一条记录的map。而不是对象
            MapHandler handler = new MapHandler();
            Map<String, Object> map = queryRunner.query(conn, sql, handler, 20);
            System.out.println(map); // {name=新垣结衣, birth=2020-11-06, id=20, email=gakki@qq.com}

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    /*
    测试查询
    MapListHandler:用于以List<Map>的形式封装多条记录
    将字段及相应字段的值作为mao中key和value，将这些map添加到List中
     */
    @Test
    public void testQuery4(){
        Connection conn = null;
        try {
            QueryRunner queryRunner = new QueryRunner();
            // 从连接池获取一个连接
            conn = JDBCPoolUtils.getConnectionFromDruid();
            String sql = "select id, name, birth, email from customers where id > ?;";

            // 获取封装表中多条条记录的List<map>
            MapListHandler handler = new MapListHandler();
            List<Map<String, Object>> mapList = queryRunner.query(conn, sql, handler, 20);
            mapList.forEach(System.out::println);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }


    // 其他查询， ScalarHandler 查询单个值对象
    @Test
    public void testQuery5(){
        Connection conn = null;
        try {
            QueryRunner queryRunner = new QueryRunner();
            // 从连接池获取一个连接
            conn = JDBCPoolUtils.getConnectionFromDruid();
            String sql = "select count(*) from customers;";

            // 获取单个值的对象
            ScalarHandler handler = new ScalarHandler();
            Long count = (Long) queryRunner.query(conn, sql, handler);
            System.out.println(count);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    // 其他查询， ScalarHandler 查询单个值对象
    @Test
    public void testQuery6(){
        Connection conn = null;
        try {
            QueryRunner queryRunner = new QueryRunner();
            // 从连接池获取一个连接
            conn = JDBCPoolUtils.getConnectionFromDruid();
            String sql = "select max(birth) from customers;";

            // 获取单个值的对象
            ScalarHandler handler = new ScalarHandler();
            java.sql.Date maxBirth = (java.sql.Date) queryRunner.query(conn, sql, handler);
            System.out.println(maxBirth);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }


    // 自定义handler
    @Test
    public void testQuery7(){
        Connection conn = null;
        try {
            QueryRunner queryRunner = new QueryRunner();
            // 从连接池获取一个连接
            conn = JDBCPoolUtils.getConnectionFromDruid();
            String sql = "select id, name, birth, email from customers where id = ?;";


            // 自定义handler，接口匿名实现类
            ResultSetHandler<Customer> handler = new ResultSetHandler<Customer>() {
                @Override
                public Customer handle(ResultSet resultSet) throws SQLException {
                    // 这个方法返回值其实就是作为下面query的返回值

                    // 下面的代码其实和源码里差不多，就是结果集中的内容封装成泛型中的对象返回出去
                    // 那些List其实就是将if改为while而已
                    if (resultSet.next()) {
                        int id = resultSet.getInt("id"); // 按字段索引位置来提取值，也是从1开始
                        String name = resultSet.getString("name");
                        String email = resultSet.getString("email"); // 可以通过字段名或别名来提取值
                        Date birth = resultSet.getDate("birth"); // 这个是java.sql.date

                        return new Customer(id, name, email, birth);
                    }

                    return null;
                }
            };
            Customer customer = queryRunner.query(conn, sql, handler, 20);
            System.out.println(customer);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }
}
