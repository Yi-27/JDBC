package com.example.crud;

import com.example.utils.JDBCUtils;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-07 10:12
 * @Description: 批量插入测试
 */
public class InsertTest {

    // 方式二：方式一是用statement
    @Test
    public void test(){

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();
            String sql = "insert into goods(name) values(?);";
            ps = conn.prepareStatement(sql);

            // 批量插入
            for (int i = 1; i <= 1000000; i++) {
                ps.setObject(1, "name_"+i);
                ps.execute(); // 每一条数据都执行一次
            }

            long end = System.currentTimeMillis();

            System.out.println("花费的时间为：" + (end - start)); // 花费的时间为：15893
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            JDBCUtils.closeResource(conn, ps);
        }
    }

    // 方式三
    @Test
    public void test2(){

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();

            String sql = "insert into goods(`NAME`) values(?)";
            ps = conn.prepareStatement(sql);

            // 批量插入
            for (int i = 1; i <= 100000000; i++) {
                ps.setObject(1, "name_"+i);

                // 1. 攒 "sql"
                ps.addBatch();

                if (i % 1000000 == 0) { // 每500执行一次 控制这个数也可以提高点速度
                    // 2. 执行batch
//                    ps.executeBatch();
                    ps.executeLargeBatch();

                    // 3. 清空已执行的batch
                    ps.clearBatch();
                }
            }

            long end = System.currentTimeMillis();

            System.out.println("花费的时间为：" + (end - start)); // 花费的时间为：5263
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            JDBCUtils.closeResource(conn, ps);
        }
    }

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


}
