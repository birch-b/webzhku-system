package com.taobao.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.taobao.util.DBUtil;

/**
 * 物流数据访问层
 */
public class LogisticsDAO {

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    /**
     * 根据订单ID查询物流信息
     */
    public Map<String, Object> getLogisticsByOrderId(long orderId) {
        String sql = "SELECT * FROM logistics WHERE order_id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractLogistics(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * 创建物流记录
     */
    public boolean createLogistics(long orderId, String company, String trackingNo,
                                    String receiverName, String receiverPhone, String receiverAddress) {
        String sql = "INSERT INTO logistics (order_id, company, tracking_no, receiver_name, " +
                     "receiver_phone, receiver_address, status, ship_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 0, NOW())";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            ps.setString(2, company);
            ps.setString(3, trackingNo);
            ps.setString(4, receiverName);
            ps.setString(5, receiverPhone);
            ps.setString(6, receiverAddress);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 更新物流信息（录入/修改快递单号）
     */
    public boolean updateLogistics(long orderId, String company, String trackingNo) {
        String sql = "UPDATE logistics SET company = ?, tracking_no = ?, status = 1, " +
                     "ship_time = COALESCE(ship_time, NOW()), update_time = NOW() " +
                     "WHERE order_id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, company);
            ps.setString(2, trackingNo);
            ps.setLong(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 更新物流状态
     */
    public boolean updateStatus(long orderId, int status) {
        String sql = "UPDATE logistics SET status = ?, update_time = NOW() WHERE order_id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setLong(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 检查物流记录是否存在
     */
    public boolean exists(long orderId) {
        String sql = "SELECT COUNT(*) FROM logistics WHERE order_id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return false;
    }

    /**
     * 从ResultSet提取物流信息
     */
    private Map<String, Object> extractLogistics(ResultSet rs) throws SQLException {
        Map<String, Object> l = new HashMap<>();
        l.put("id", rs.getLong("id"));
        l.put("order_id", rs.getLong("order_id"));
        l.put("company", rs.getString("company"));
        l.put("tracking_no", rs.getString("tracking_no"));
        l.put("receiver_name", rs.getString("receiver_name"));
        l.put("receiver_phone", rs.getString("receiver_phone"));
        l.put("receiver_address", rs.getString("receiver_address"));
        l.put("status", rs.getInt("status"));
        l.put("ship_time", rs.getTimestamp("ship_time"));
        l.put("create_time", rs.getTimestamp("create_time"));
        l.put("update_time", rs.getTimestamp("update_time"));
        return l;
    }

    public void close() {
        DBUtil.close(conn, ps, rs);
    }
}
