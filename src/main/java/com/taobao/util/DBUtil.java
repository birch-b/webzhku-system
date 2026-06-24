package com.taobao.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接工具类
 * 使用 Druid 连接池管理数据库连接
 */
public class DBUtil {

    private static DruidDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            if (is == null) {
                // 如果没有配置文件，使用默认配置
                props.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
                props.setProperty("url", "jdbc:mysql://localhost:3306/taobao_shop?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8");
                props.setProperty("username", "root");
                props.setProperty("password", "root");
                props.setProperty("initialSize", "5");
                props.setProperty("maxActive", "20");
                props.setProperty("minIdle", "5");
                props.setProperty("maxWait", "60000");
            } else {
                props.load(is);
                is.close();
            }
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("数据库连接池初始化失败", e);
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 关闭资源
     */
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接和语句
     */
    public static void close(Connection conn, PreparedStatement ps) {
        close(conn, ps, null);
    }

    /**
     * 获取数据源
     */
    public static DataSource getDataSource() {
        return dataSource;
    }
}
