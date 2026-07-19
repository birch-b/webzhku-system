package com.taobao.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.util.DBUtil;

public class OrderDAO {

    public List<Map<String, Object>> getOrdersByShopId(long shopId, Integer status, int page, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT o.*, u.nickname as buyer_nickname ");
        sql.append("FROM `order` o LEFT JOIN `user` u ON o.user_id = u.id ");
        sql.append("WHERE o.shop_id = ? ");
        if (status != null) sql.append("AND o.status = ? ");
        sql.append("ORDER BY o.create_time DESC LIMIT ? OFFSET ?");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setLong(idx++, shopId);
            if (status != null) ps.setInt(idx++, status);
            ps.setInt(idx++, pageSize);
            ps.setInt(idx++, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> order = extractOrder(rs);
                    order.put("items", getOrderItems(conn, rs.getLong("id")));
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺订单列表失败", e);
        }
        return list;
    }

    public int getOrderCount(long shopId, Integer status) {
        String sql = status != null
            ? "SELECT COUNT(*) FROM `order` WHERE shop_id = ? AND status = ?"
            : "SELECT COUNT(*) FROM `order` WHERE shop_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            if (status != null) ps.setInt(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单数量失败", e);
        }
        return 0;
    }

    public Map<String, Object> getOrderById(long orderId) {
        String sql = "SELECT o.*, u.nickname as buyer_nickname, u.phone as buyer_phone " +
                     "FROM `order` o LEFT JOIN `user` u ON o.user_id = u.id WHERE o.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> order = extractOrder(rs);
                    order.put("items", getOrderItems(conn, orderId));
                    return order;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单失败", e);
        }
        return null;
    }

    private List<Map<String, Object>> getOrderItems(Connection conn, long orderId) throws SQLException {
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        try (PreparedStatement ps2 = conn.prepareStatement(sql)) {
            ps2.setLong(1, orderId);
            try (ResultSet rs2 = ps2.executeQuery()) {
                while (rs2.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", rs2.getLong("id"));
                    item.put("order_id", rs2.getLong("order_id"));
                    item.put("product_id", rs2.getLong("product_id"));
                    item.put("product_name", rs2.getString("product_name"));
                    item.put("cover_image", rs2.getString("cover_image"));
                    item.put("price", rs2.getDouble("price"));
                    item.put("quantity", rs2.getInt("quantity"));
                    item.put("subtotal", rs2.getDouble("subtotal"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    public boolean shipOrder(long orderId) {
        String sql = "UPDATE `order` SET status = 2, ship_time = NOW() WHERE id = ? AND status = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("发货失败", e);
        }
    }

    public boolean confirmReceive(long orderId) {
        String sql = "UPDATE `order` SET status = 3, receive_time = NOW() WHERE id = ? AND status = 2";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("确认收货失败", e);
        }
    }

    public boolean finishOrder(long orderId) {
        String sql = "UPDATE `order` SET status = 4, finish_time = NOW() WHERE id = ? AND status = 3";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("完成订单失败", e);
        }
    }

    public Map<String, Integer> getOrderStats(long shopId) {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as cnt FROM `order` WHERE shop_id = ? GROUP BY status";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.put("status_" + rs.getInt("status"), rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单统计失败", e);
        }
        return stats;
    }

    private Map<String, Object> extractOrder(ResultSet rs) throws SQLException {
        Map<String, Object> order = new HashMap<>();
        order.put("id", rs.getLong("id"));
        order.put("order_no", rs.getString("order_no"));
        order.put("user_id", rs.getLong("user_id"));
        order.put("shop_id", rs.getLong("shop_id"));
        order.put("buyer_nickname", rs.getString("buyer_nickname"));
        order.put("total_amount", rs.getDouble("total_amount"));
        order.put("discount_amount", rs.getDouble("discount_amount"));
        order.put("pay_amount", rs.getDouble("pay_amount"));
        order.put("pay_method", rs.getObject("pay_method"));
        order.put("status", rs.getInt("status"));
        order.put("receiver_name", rs.getString("receiver_name"));
        order.put("receiver_phone", rs.getString("receiver_phone"));
        order.put("receiver_address", rs.getString("receiver_address"));
        order.put("buyer_message", rs.getString("buyer_message"));
        order.put("create_time", rs.getTimestamp("create_time"));
        order.put("pay_time", rs.getTimestamp("pay_time"));
        order.put("ship_time", rs.getTimestamp("ship_time"));
        order.put("receive_time", rs.getTimestamp("receive_time"));
        order.put("finish_time", rs.getTimestamp("finish_time"));
        return order;
    }

    public List<Map<String, Object>> listAllOrder(String status, String keyword, int page) {
        List<Map<String, Object>> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.nickname AS buyer_name, s.shop_name FROM `order` o " +
                "LEFT JOIN user u ON o.user_id = u.id " +
                "LEFT JOIN shop s ON o.shop_id = s.id WHERE 1=1";
        List<Object> params = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            sql += " AND o.status = ?";
            params.add(Integer.parseInt(status));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = "%" + keyword.trim() + "%";
            sql += " AND (o.order_no LIKE ? OR u.nickname LIKE ?)";
            params.add(kw);
            params.add(kw);
        }
        sql += " ORDER BY o.create_time DESC LIMIT ?, 20";
        params.add((page - 1) * 20);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", rs.getLong("id"));
                    m.put("orderNo", rs.getString("order_no"));
                    m.put("buyerName", rs.getString("buyer_name"));
                    m.put("shopName", rs.getString("shop_name"));
                    m.put("totalAmount", rs.getBigDecimal("total_amount"));
                    m.put("payAmount", rs.getBigDecimal("pay_amount"));
                    m.put("status", rs.getInt("status"));
                    m.put("createTime", rs.getTimestamp("create_time"));
                    orders.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单列表失败", e);
        }
        return orders;
    }

    public List<Map<String, Object>> listAbnormalOrder(String keyword, int page) {
        List<Map<String, Object>> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.nickname AS buyer_name, s.shop_name FROM `order` o " +
                "LEFT JOIN user u ON o.user_id = u.id " +
                "LEFT JOIN shop s ON o.shop_id = s.id WHERE o.status IN (5,6,7)";
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = "%" + keyword.trim() + "%";
            sql += " AND (o.order_no LIKE ? OR u.nickname LIKE ?)";
            params.add(kw);
            params.add(kw);
        }
        sql += " ORDER BY o.create_time DESC LIMIT ?, 20";
        params.add((page - 1) * 20);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", rs.getLong("id"));
                    m.put("orderNo", rs.getString("order_no"));
                    m.put("buyerName", rs.getString("buyer_name"));
                    m.put("shopName", rs.getString("shop_name"));
                    m.put("totalAmount", rs.getBigDecimal("total_amount"));
                    m.put("payAmount", rs.getBigDecimal("pay_amount"));
                    m.put("status", rs.getInt("status"));
                    m.put("createTime", rs.getTimestamp("create_time"));
                    orders.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询异常订单失败", e);
        }
        return orders;
    }

    public Map<String, Object> getOrderDetailById(Long orderId) {
        Map<String, Object> o = null;
        String sql = "SELECT o.*, u.nickname AS buyer_name, u.phone AS buyer_phone, " +
                "s.shop_name, l.company, l.tracking_no, l.status AS logistics_status " +
                "FROM `order` o LEFT JOIN user u ON o.user_id = u.id " +
                "LEFT JOIN shop s ON o.shop_id = s.id " +
                "LEFT JOIN logistics l ON o.id = l.order_id WHERE o.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    o = new HashMap<>();
                    o.put("id", rs.getLong("id"));
                    o.put("orderNo", rs.getString("order_no"));
                    o.put("buyerName", rs.getString("buyer_name"));
                    o.put("buyerPhone", rs.getString("buyer_phone"));
                    o.put("shopName", rs.getString("shop_name"));
                    o.put("totalAmount", rs.getBigDecimal("total_amount"));
                    o.put("payAmount", rs.getBigDecimal("pay_amount"));
                    o.put("status", rs.getInt("status"));
                    o.put("receiverName", rs.getString("receiver_name"));
                    o.put("receiverPhone", rs.getString("receiver_phone"));
                    o.put("receiverAddress", rs.getString("receiver_address"));
                    o.put("buyerMessage", rs.getString("buyer_message"));
                    o.put("createTime", rs.getTimestamp("create_time"));
                    o.put("payTime", rs.getTimestamp("pay_time"));
                    o.put("shipTime", rs.getTimestamp("ship_time"));
                    o.put("logisticsCompany", rs.getString("company"));
                    o.put("trackingNo", rs.getString("tracking_no"));
                    o.put("logisticsStatus", rs.getInt("logistics_status"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单详情失败", e);
        }
        return o;
    }

    public List<Map<String, Object>> listOrderItemByOrderId(Long orderId) {
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> it = new HashMap<>();
                    it.put("productName", rs.getString("product_name"));
                    it.put("coverImage", rs.getString("cover_image"));
                    it.put("price", rs.getBigDecimal("price"));
                    it.put("quantity", rs.getInt("quantity"));
                    it.put("subtotal", rs.getBigDecimal("subtotal"));
                    items.add(it);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单项失败", e);
        }
        return items;
    }

    public Map<String, Object> getCheckoutData(Long userId) {
        Map<String, Object> data = new HashMap<>();
        try (Connection conn = DBUtil.getConnection()) {
            String cartSql = "SELECT ci.*, p.name, p.price, p.cover_image, p.shop_id, p.stock " +
                    "FROM cart_item ci LEFT JOIN product p ON ci.product_id = p.id " +
                    "WHERE ci.user_id = ? AND ci.selected = 1";
            try (PreparedStatement ps = conn.prepareStatement(cartSql)) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    List<Map<String, Object>> items = new ArrayList<>();
                    double total = 0;
                    while (rs.next()) {
                        double subtotal = rs.getDouble("price") * rs.getInt("quantity");
                        total += subtotal;
                        Map<String, Object> item = new HashMap<>();
                        item.put("productId", rs.getLong("product_id"));
                        item.put("name", rs.getString("name"));
                        item.put("price", rs.getDouble("price"));
                        item.put("quantity", rs.getInt("quantity"));
                        item.put("subtotal", subtotal);
                        item.put("coverImage", rs.getString("cover_image"));
                        item.put("shopId", rs.getLong("shop_id"));
                        item.put("stock", rs.getInt("stock"));
                        items.add(item);
                    }
                    data.put("items", items);
                    data.put("totalAmount", total);
                }
            }

            String addrSql = "SELECT id, receiver_name, phone, province, city, district, detail, is_default " +
                    "FROM address WHERE user_id = ? ORDER BY is_default DESC, create_time DESC";
            try (PreparedStatement ps = conn.prepareStatement(addrSql)) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    List<Map<String, Object>> addrs = new ArrayList<>();
                    while (rs.next()) {
                        Map<String, Object> addr = new HashMap<>();
                        addr.put("id", rs.getLong("id"));
                        addr.put("receiverName", rs.getString("receiver_name"));
                        addr.put("phone", rs.getString("phone"));
                        addr.put("fullAddress", rs.getString("province") + rs.getString("city") + rs.getString("district") + rs.getString("detail"));
                        addr.put("isDefault", rs.getInt("is_default"));
                        addrs.add(addr);
                    }
                    data.put("addresses", addrs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取结算数据失败", e);
        }
        return data;
    }

    public long createOrder(Long userId, Long addressId, String buyerMessage) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String cartSql = "SELECT ci.*, p.name, p.price, p.cover_image, p.shop_id, p.stock " +
                        "FROM cart_item ci LEFT JOIN product p ON ci.product_id = p.id " +
                        "WHERE ci.user_id = ? AND ci.selected = 1";
                List<Map<String, Object>> items = new ArrayList<>();
                double total = 0;
                long shopId = 0;
                try (PreparedStatement ps = conn.prepareStatement(cartSql)) {
                    ps.setLong(1, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int stock = rs.getInt("stock");
                            int qty = rs.getInt("quantity");
                            if (qty > stock) {
                                conn.rollback();
                                throw new RuntimeException("商品库存不足");
                            }
                            shopId = rs.getLong("shop_id");
                            double price = rs.getDouble("price");
                            total += price * qty;
                            Map<String, Object> item = new HashMap<>();
                            item.put("productId", rs.getLong("product_id"));
                            item.put("name", rs.getString("name"));
                            item.put("price", price);
                            item.put("quantity", qty);
                            item.put("subtotal", price * qty);
                            item.put("coverImage", rs.getString("cover_image"));
                            items.add(item);
                        }
                    }
                }
                if (items.isEmpty()) {
                    conn.rollback();
                    throw new RuntimeException("购物车为空");
                }

                // 越权防护：校验收货地址必须归属当前 userId，防止顾客猜解他人 addressId
                String addrSql = "SELECT * FROM address WHERE id = ? AND user_id = ?";
                String receiverName = "", receiverPhone = "", receiverAddress = "";
                boolean addressValid = false;
                try (PreparedStatement ps = conn.prepareStatement(addrSql)) {
                    ps.setLong(1, addressId);
                    ps.setLong(2, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            addressValid = true;
                            receiverName = rs.getString("receiver_name");
                            receiverPhone = rs.getString("phone");
                            receiverAddress = rs.getString("province") + rs.getString("city") + rs.getString("district") + rs.getString("detail");
                        }
                    }
                }
                if (!addressValid) {
                    conn.rollback();
                    throw new RuntimeException("收货地址不存在或无权使用");
                }

                String orderNo = "TB" + System.currentTimeMillis() + userId;
                String insertOrderSql = "INSERT INTO `order`(order_no, user_id, shop_id, total_amount, pay_amount, status, receiver_name, receiver_phone, receiver_address, buyer_message, create_time) VALUES(?, ?, ?, ?, ?, 0, ?, ?, ?, ?, NOW())";
                try (PreparedStatement ps = conn.prepareStatement(insertOrderSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, orderNo);
                    ps.setLong(2, userId);
                    ps.setLong(3, shopId);
                    ps.setDouble(4, total);
                    ps.setDouble(5, total);
                    ps.setString(6, receiverName);
                    ps.setString(7, receiverPhone);
                    ps.setString(8, receiverAddress);
                    ps.setString(9, buyerMessage != null ? buyerMessage : "");
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            long orderId = rs.getLong(1);
                            for (Map<String, Object> item : items) {
                                String insertItemSql = "INSERT INTO order_item(order_id, product_id, product_name, cover_image, price, quantity, subtotal) VALUES(?, ?, ?, ?, ?, ?, ?)";
                                try (PreparedStatement ps2 = conn.prepareStatement(insertItemSql)) {
                                    ps2.setLong(1, orderId);
                                    ps2.setLong(2, (Long) item.get("productId"));
                                    ps2.setString(3, (String) item.get("name"));
                                    ps2.setString(4, (String) item.get("coverImage"));
                                    ps2.setDouble(5, (Double) item.get("price"));
                                    ps2.setInt(6, (Integer) item.get("quantity"));
                                    ps2.setDouble(7, (Double) item.get("subtotal"));
                                    ps2.executeUpdate();
                                }
                            }
                            for (Map<String, Object> item : items) {
                                String updateStockSql = "UPDATE product SET stock = stock - ? WHERE id = ? AND stock >= ?";
                                try (PreparedStatement ps2 = conn.prepareStatement(updateStockSql)) {
                                    ps2.setInt(1, (Integer) item.get("quantity"));
                                    ps2.setLong(2, (Long) item.get("productId"));
                                    ps2.setInt(3, (Integer) item.get("quantity"));
                                    if (ps2.executeUpdate() == 0) {
                                        conn.rollback();
                                        throw new RuntimeException("商品库存不足");
                                    }
                                }
                            }
                            String deleteCartSql = "DELETE FROM cart_item WHERE user_id = ? AND selected = 1";
                            try (PreparedStatement ps2 = conn.prepareStatement(deleteCartSql)) {
                                ps2.setLong(1, userId);
                                ps2.executeUpdate();
                            }
                            conn.commit();
                            return orderId;
                        }
                    }
                }
                conn.rollback();
                throw new RuntimeException("创建订单失败");
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("创建订单失败", e);
        }
    }

    public Map<String, Object> listUserOrders(Long userId, String status, int page, int pageSize) {
        Map<String, Object> data = new HashMap<>();
        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder where = new StringBuilder(" WHERE o.user_id = ?");
            List<Object> params = new ArrayList<>();
            params.add(userId);
            if (status != null && !status.isEmpty()) {
                where.append(" AND o.status = ?");
                params.add(Integer.parseInt(status));
            }
            String countSql = "SELECT COUNT(*) FROM `order` o" + where;
            int total = 0;
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) total = rs.getInt(1);
                }
            }
            int pages = (int) Math.ceil((double) total / pageSize);
            String listSql = "SELECT o.*, s.shop_name FROM `order` o LEFT JOIN shop s ON o.shop_id = s.id" + where + " ORDER BY o.create_time DESC LIMIT ?, ?";
            List<Map<String, Object>> orders = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(listSql)) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                ps.setInt(params.size() + 1, (page - 1) * pageSize);
                ps.setInt(params.size() + 2, pageSize);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("id", rs.getLong("id"));
                        m.put("orderNo", rs.getString("order_no"));
                        m.put("shopName", rs.getString("shop_name"));
                        m.put("totalAmount", rs.getDouble("total_amount"));
                        m.put("status", rs.getInt("status"));
                        m.put("createTime", rs.getTimestamp("create_time"));
                        m.put("payAmount", rs.getBigDecimal("pay_amount"));
                        orders.add(m);
                    }
                }
            }
            data.put("orders", orders);
            data.put("totalPages", pages);
            data.put("totalCount", total);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单列表失败", e);
        }
        return data;
    }

    public Map<String, Object> getUserOrderDetail(Long orderId, Long userId) {
        Map<String, Object> data = new HashMap<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT o.*, s.shop_name, l.company, l.tracking_no, l.status AS logistics_status " +
                    "FROM `order` o LEFT JOIN shop s ON o.shop_id = s.id " +
                    "LEFT JOIN logistics l ON o.id = l.order_id WHERE o.id = ? AND o.user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, orderId);
                ps.setLong(2, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data.put("id", rs.getLong("id"));
                        data.put("orderNo", rs.getString("order_no"));
                        data.put("shopName", rs.getString("shop_name"));
                        data.put("totalAmount", rs.getBigDecimal("total_amount"));
                        data.put("payAmount", rs.getBigDecimal("pay_amount"));
                        data.put("status", rs.getInt("status"));
                        data.put("receiverName", rs.getString("receiver_name"));
                        data.put("receiverPhone", rs.getString("receiver_phone"));
                        data.put("receiverAddress", rs.getString("receiver_address"));
                        data.put("buyerMessage", rs.getString("buyer_message"));
                        data.put("createTime", rs.getTimestamp("create_time"));
                        data.put("payTime", rs.getTimestamp("pay_time"));
                        data.put("shipTime", rs.getTimestamp("ship_time"));
                        data.put("receiveTime", rs.getTimestamp("receive_time"));
                        data.put("finishTime", rs.getTimestamp("finish_time"));
                        data.put("company", rs.getString("company"));
                        data.put("trackingNo", rs.getString("tracking_no"));
                        data.put("logisticsStatus", rs.getInt("logistics_status"));
                    } else {
                        throw new RuntimeException("订单不存在");
                    }
                }
            }
            String itemSql = "SELECT * FROM order_item WHERE order_id = ?";
            List<Map<String, Object>> items = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                ps.setLong(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> it = new HashMap<>();
                        it.put("productId", rs.getLong("product_id"));
                        it.put("productName", rs.getString("product_name"));
                        it.put("coverImage", rs.getString("cover_image"));
                        it.put("price", rs.getString("price"));
                        it.put("quantity", rs.getInt("quantity"));
                        it.put("subtotal", rs.getString("subtotal"));
                        items.add(it);
                    }
                }
            }
            data.put("items", items);
            String aftersaleSql = "SELECT id FROM aftersale WHERE order_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(aftersaleSql)) {
                ps.setLong(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    data.put("hasAftersale", rs.next());
                }
            }
            String reviewSql = "SELECT id FROM review WHERE order_id = ? AND user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(reviewSql)) {
                ps.setLong(1, orderId);
                ps.setLong(2, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    data.put("hasReview", rs.next());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单详情失败", e);
        }
        return data;
    }

    public void cancelOrder(Long orderId, Long userId) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String updateSql = "UPDATE `order` SET status = 5 WHERE id = ? AND user_id = ? AND status = 0";
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setLong(1, orderId);
                    ps.setLong(2, userId);
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        String itemSql = "SELECT product_id, quantity FROM order_item WHERE order_id = ?";
                        try (PreparedStatement ps2 = conn.prepareStatement(itemSql)) {
                            ps2.setLong(1, orderId);
                            try (ResultSet rs = ps2.executeQuery()) {
                                while (rs.next()) {
                                    String stockSql = "UPDATE product SET stock = stock + ? WHERE id = ?";
                                    try (PreparedStatement ps3 = conn.prepareStatement(stockSql)) {
                                        ps3.setInt(1, rs.getInt("quantity"));
                                        ps3.setLong(2, rs.getLong("product_id"));
                                        ps3.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("取消订单失败", e);
        }
    }

    public void confirmOrder(Long orderId, Long userId) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String updateOrderSql = "UPDATE `order` SET status = 3, receive_time = NOW() WHERE id = ? AND user_id = ? AND status = 2";
                try (PreparedStatement ps = conn.prepareStatement(updateOrderSql)) {
                    ps.setLong(1, orderId);
                    ps.setLong(2, userId);
                    ps.executeUpdate();
                }
                String updateLogisticsSql = "UPDATE logistics SET status = 3 WHERE order_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateLogisticsSql)) {
                    ps.setLong(1, orderId);
                    ps.executeUpdate();
                }
                String itemSql = "SELECT product_id, quantity FROM order_item WHERE order_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                    ps.setLong(1, orderId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String salesSql = "UPDATE product SET sales = sales + ? WHERE id = ?";
                            try (PreparedStatement ps2 = conn.prepareStatement(salesSql)) {
                                ps2.setInt(1, rs.getInt("quantity"));
                                ps2.setLong(2, rs.getLong("product_id"));
                                ps2.executeUpdate();
                            }
                        }
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("确认收货失败", e);
        }
    }

    public boolean payOrder(Long orderId, Long userId, int payMethod) {
        try (Connection conn = DBUtil.getConnection()) {
            String checkSql = "SELECT user_id, total_amount FROM `order` WHERE id = ? AND status = 0";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setLong(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return false;
                    }
                    if (rs.getLong("user_id") != userId) {
                        return false;
                    }
                }
            }
            String updateSql = "UPDATE `order` SET status = 1, pay_method = ?, pay_time = NOW() WHERE id = ? AND status = 0";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, payMethod);
                ps.setLong(2, orderId);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    String insertLogisticsSql = "INSERT INTO logistics(order_id, status) VALUES(?, 0)";
                    try (PreparedStatement ps2 = conn.prepareStatement(insertLogisticsSql)) {
                        ps2.setLong(1, orderId);
                        ps2.executeUpdate();
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("支付失败", e);
        }
        return false;
    }

    /**
     * 商家查询订单列表（无分页，按状态可选过滤）
     * 返回字段：id, order_no, buyer_name, pay_amount, status, create_time, receiver_name
     */
    public List<Map<String, Object>> listShopOrders(long shopId, Integer status) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT o.*, u.nickname AS buyer_name FROM `order` o LEFT JOIN user u ON o.user_id = u.id WHERE o.shop_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(shopId);
        if (status != null) {
            sql.append(" AND o.status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY o.create_time DESC");
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> o = new HashMap<>();
                    o.put("id", rs.getLong("id"));
                    o.put("order_no", rs.getString("order_no"));
                    o.put("buyer_name", rs.getString("buyer_name"));
                    o.put("pay_amount", rs.getString("pay_amount"));
                    o.put("status", rs.getInt("status"));
                    o.put("create_time", rs.getTimestamp("create_time"));
                    o.put("receiver_name", rs.getString("receiver_name"));
                    list.add(o);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询商家订单列表失败", e);
        }
        return list;
    }

    /**
     * 商家查询订单详情（含买家昵称、物流信息）
     * 返回字段：id, order_no, buyer_name, pay_amount, status, receiver_name, receiver_phone,
     *           receiver_address, company, tracking_no, logistics_status, create_time
     * 越权防护：必须同时校验订单归属当前 shopId
     */
    public Map<String, Object> getShopOrderDetail(long orderId, long shopId) {
        Map<String, Object> order = null;
        String sql = "SELECT o.*, u.nickname AS buyer_name, l.company, l.tracking_no, l.status AS logistics_status " +
                     "FROM `order` o LEFT JOIN user u ON o.user_id = u.id " +
                     "LEFT JOIN logistics l ON o.id = l.order_id WHERE o.id = ? AND o.shop_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.setLong(2, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = new HashMap<>();
                    order.put("id", rs.getLong("id"));
                    order.put("order_no", rs.getString("order_no"));
                    order.put("buyer_name", rs.getString("buyer_name"));
                    order.put("pay_amount", rs.getString("pay_amount"));
                    order.put("status", rs.getInt("status"));
                    order.put("receiver_name", rs.getString("receiver_name"));
                    order.put("receiver_phone", rs.getString("receiver_phone"));
                    order.put("receiver_address", rs.getString("receiver_address"));
                    order.put("company", rs.getString("company"));
                    order.put("tracking_no", rs.getString("tracking_no"));
                    order.put("logistics_status", rs.getInt("logistics_status"));
                    order.put("create_time", rs.getTimestamp("create_time"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询商家订单详情失败", e);
        }
        return order;
    }

    /**
     * 查询订单项商品列表（商家端使用，与原Servlet字段保持一致）
     * 返回字段：product_name, cover_image, price, quantity, subtotal
     */
    public List<Map<String, Object>> getOrderItemsByOrderId(long orderId) {
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> it = new HashMap<>();
                    it.put("product_name", rs.getString("product_name"));
                    it.put("cover_image", rs.getString("cover_image"));
                    it.put("price", rs.getString("price"));
                    it.put("quantity", rs.getInt("quantity"));
                    it.put("subtotal", rs.getString("subtotal"));
                    items.add(it);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单项失败", e);
        }
        return items;
    }

    /**
     * 商家发货（事务）：更新订单状态为已发货 + 写入/更新物流记录
     * 越权防护：UPDATE 必须同时校验订单归属当前 shopId
     */
    public void shipOrder(long orderId, long shopId, String company, String trackingNo) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps1 = conn.prepareStatement(
                    "UPDATE `order` SET status = 2, ship_time = NOW() WHERE id = ? AND shop_id = ? AND status = 1")) {
                    ps1.setLong(1, orderId);
                    ps1.setLong(2, shopId);
                    ps1.executeUpdate();
                }
                try (PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO logistics (order_id, company, tracking_no, status, ship_time) VALUES (?, ?, ?, 1, NOW()) " +
                    "ON DUPLICATE KEY UPDATE company=?, tracking_no=?, status=1, ship_time=NOW()")) {
                    ps2.setLong(1, orderId);
                    ps2.setString(2, company);
                    ps2.setString(3, trackingNo);
                    ps2.setString(4, company);
                    ps2.setString(5, trackingNo);
                    ps2.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("商家发货失败", e);
        }
    }

    /**
     * 商家取消订单（事务）：仅取消未付款订单，并恢复商品库存
     */
    public void cancelShopOrder(long orderId, long shopId) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int rows;
                try (PreparedStatement ps1 = conn.prepareStatement(
                    "UPDATE `order` SET status = 5 WHERE id = ? AND shop_id = ? AND status = 0")) {
                    ps1.setLong(1, orderId);
                    ps1.setLong(2, shopId);
                    rows = ps1.executeUpdate();
                }
                if (rows > 0) {
                    try (PreparedStatement ps2 = conn.prepareStatement(
                        "SELECT product_id, quantity FROM order_item WHERE order_id = ?")) {
                        ps2.setLong(1, orderId);
                        try (ResultSet rs = ps2.executeQuery()) {
                            while (rs.next()) {
                                try (PreparedStatement ps3 = conn.prepareStatement(
                                    "UPDATE product SET stock = stock + ? WHERE id = ?")) {
                                    ps3.setInt(1, rs.getInt("quantity"));
                                    ps3.setLong(2, rs.getLong("product_id"));
                                    ps3.executeUpdate();
                                }
                            }
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("商家取消订单失败", e);
        }
    }
}
