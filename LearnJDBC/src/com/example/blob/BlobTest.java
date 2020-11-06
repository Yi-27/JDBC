package com.example.blob;

import com.example.bean.Customer;
import com.example.utils.JDBCUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-06 21:37
 * @Description:
 */
public class BlobTest {

    // 向数据表customers中插入Blob类型的字段
    @Test
    public void testInsert(){
        Connection conn = null;
        PreparedStatement ps = null;
        FileInputStream fis = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "insert into customers(name, email, birth, photo) values(?, ?, ?, ?);";

            ps = conn.prepareStatement(sql);

            ps.setObject(1, "Gakki");
            ps.setObject(2, "Gakki@qq.com");
            ps.setObject(3, "1988-06-11");

            // 打开图片，该图片地址在项目根目录下
            fis = new FileInputStream("gakki.png");
            ps.setBlob(4, fis);

            int i = ps.executeUpdate();
            if (i > 0){
                System.out.println(i);
                System.out.println("数据插入成功");
            }else {
                System.out.println("数据插入失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JDBCUtils.closeResource(conn, ps);
        }
    }

    // 从数据库中读取Blob类型的字段
    @Test
    public void testSelect(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        InputStream binaryStream = null;
        FileOutputStream fos = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "select id, name, birth, email, photo from customers where id = ?;";
            ps = conn.prepareStatement(sql);
            ps.setObject(1, 22);

            // 执行得到结果集
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                java.sql.Date birth = resultSet.getDate("birth");

                // 封装进对象
                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);
                // 获取Blob对象
                Blob photo = resultSet.getBlob("photo");
                // 获取blob对象的二进制流对象
                binaryStream = photo.getBinaryStream(); // 这里的输入流相当于从 数据库 输入到 Java程序中
                // 构建输出流对象
                fos = new FileOutputStream("src/gakki2.png"); // 位置从项目目录下开始

                // 一次读字节流的大小
                byte[] buf = new byte[1024];
                int len;
                while((len = binaryStream.read(buf)) != -1){
                    // 写入文件中
                    fos.write(buf, 0, len);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (binaryStream != null)
                    binaryStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JDBCUtils.closeResource(conn, ps, resultSet);
        }


    }
}
