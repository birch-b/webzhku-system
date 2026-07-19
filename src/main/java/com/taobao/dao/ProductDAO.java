package com.taobao.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.taobao.util.DBUtil;

public class ProductDAO {

    public List<Map<String, Object>> getProductsByShopId(long shopId, int status, int page, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.*, c.name as category_name FROM product p ");
        sql.append("LEFT JOIN category c ON p.category_id = c.id ");
        sql.append("WHERE p.shop_id = ? ");
        if (status >= 0) sql.append("AND p.status = ? ");
        sql.append("ORDER BY p.update_time DESC LIMIT ? OFFSET ?");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setLong(idx++, shopId);
            if (status >= 0) ps.setInt(idx++, status);
            ps.setInt(idx++, pageSize);
            ps.setInt(idx++, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> p = extractProduct(rs);
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺商品列表失败", e);
        }
        return list;
    }

    public int getProductCount(long shopId, int status) {
        String sql = status >= 0
            ? "SELECT COUNT(*) FROM product WHERE shop_id = ? AND status = ?"
            : "SELECT COUNT(*) FROM product WHERE shop_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            if (status >= 0) ps.setInt(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询商品数量失败", e);
        }
        return 0;
    }

    public Map<String, Object> getProductById(long id) {
        String sql = "SELECT p.*, c.name as category_name FROM product p " +
                     "LEFT JOIN category c ON p.category_id = c.id WHERE p.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractProduct(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询商品失败", e);
        }
        return null;
    }

    public long addProduct(long shopId, long categoryId, String name, String subtitle,
                           String description, double price, double originalPrice, int stock,
                           String images, String coverImage) {
        String sql = "INSERT INTO product (shop_id, category_id, name, subtitle, description, " +
                     "price, original_price, stock, images, cover_image, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, shopId);
            if (categoryId > 0) ps.setLong(2, categoryId); else ps.setNull(2, java.sql.Types.BIGINT);
            ps.setString(3, name);
            ps.setString(4, subtitle);
            ps.setString(5, description);
            ps.setDouble(6, price);
            ps.setDouble(7, originalPrice);
            ps.setInt(8, stock);
            ps.setString(9, images);
            ps.setString(10, coverImage);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("新增商品失败", e);
        }
        return -1;
    }

    public boolean updateProduct(long id, long categoryId, String name, String subtitle,
                                  String description, double price, double originalPrice,
                                  String images, String coverImage) {
        String sql = "UPDATE product SET category_id = ?, name = ?, subtitle = ?, description = ?, " +
                     "price = ?, original_price = ?, images = ?, cover_image = ?, update_time = NOW() " +
                     "WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            if (categoryId > 0) ps.setLong(idx++, categoryId); else ps.setNull(idx++, java.sql.Types.BIGINT);
            ps.setString(idx++, name);
            ps.setString(idx++, subtitle);
            ps.setString(idx++, description);
            ps.setDouble(idx++, price);
            ps.setDouble(idx++, originalPrice);
            ps.setString(idx++, images);
            ps.setString(idx++, coverImage);
            ps.setLong(idx++, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新商品失败", e);
        }
    }

    public boolean adjustStock(long id, int delta) {
        String sql = "UPDATE product SET stock = stock + ?, update_time = NOW() WHERE id = ? AND (stock + ?) >= 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setLong(2, id);
            ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("调整库存失败", e);
        }
    }

    public boolean publishProduct(long id) {
        String sql = "UPDATE product SET status = 1, publish_time = NOW(), update_time = NOW() WHERE id = ? AND status = 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("上架商品失败", e);
        }
    }

    public boolean unpublishProduct(long id) {
        String sql = "UPDATE product SET status = 2, update_time = NOW() WHERE id = ? AND status IN (1,3)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("下架商品失败", e);
        }
    }

    public boolean deleteProduct(long id) {
        String sql = "DELETE FROM product WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除商品失败", e);
        }
    }

    public boolean updateImages(long id, String images, String coverImage) {
        String sql = "UPDATE product SET images = ?, cover_image = ?, update_time = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, images);
            ps.setString(2, coverImage);
            ps.setLong(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新商品图片失败", e);
        }
    }

    private Map<String, Object> extractProduct(ResultSet rs) throws SQLException {
        Map<String, Object> p = new HashMap<>();
        p.put("id", rs.getLong("id"));
        p.put("shop_id", rs.getLong("shop_id"));
        p.put("category_id", rs.getObject("category_id"));
        p.put("category_name", rs.getString("category_name"));
        p.put("name", rs.getString("name"));
        p.put("subtitle", rs.getString("subtitle"));
        p.put("description", rs.getString("description"));
        p.put("price", rs.getDouble("price"));
        p.put("original_price", rs.getDouble("original_price"));
        p.put("stock", rs.getInt("stock"));
        p.put("sales", rs.getInt("sales"));
        p.put("status", rs.getInt("status"));
        p.put("images", rs.getString("images"));
        p.put("cover_image", rs.getString("cover_image"));
        p.put("weight", rs.getObject("weight"));
        p.put("create_time", rs.getTimestamp("create_time"));
        p.put("update_time", rs.getTimestamp("update_time"));
        p.put("publish_time", rs.getTimestamp("publish_time"));
        try {
            String imagesStr = rs.getString("images");
            if (imagesStr != null && !imagesStr.isEmpty()) {
                JSONArray arr = new JSONArray(imagesStr);
                List<String> imageList = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    imageList.add(arr.getString(i));
                }
                p.put("imageList", imageList);
            }
        } catch (Exception e) {
            p.put("imageList", new ArrayList<>());
        }
        return p;
    }

    public List<Map<String, Object>> listByCategory(String categoryName) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.id, p.name, p.price, p.cover_image AS main_image, p.sales, s.shop_name ");
        sql.append("FROM product p LEFT JOIN shop s ON p.shop_id = s.id ");
        sql.append("LEFT JOIN category c ON p.category_id = c.id ");
        sql.append("LEFT JOIN category pc ON c.parent_id = pc.id ");
        sql.append("WHERE p.status = 1 ");

        boolean hasCat = categoryName != null && !categoryName.isEmpty();
        if (hasCat) {
            sql.append("AND (c.name = ? OR pc.name = ?) ");
        }
        sql.append("ORDER BY p.sales DESC LIMIT 12");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (hasCat) {
                ps.setString(1, categoryName);
                ps.setString(2, categoryName);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> prod = new HashMap<>();
                    prod.put("id", rs.getLong("id"));
                    prod.put("name", rs.getString("name"));
                    prod.put("price", rs.getBigDecimal("price"));
                    prod.put("main_image", rs.getString("main_image"));
                    prod.put("sales", rs.getInt("sales"));
                    prod.put("shop_name", rs.getString("shop_name"));
                    list.add(prod);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询商品列表失败", e);
        }
        return list;
    }

    public Map<String, Object> listProducts(String categoryId, String sort, int page, int pageSize) {
        Map<String, Object> data = new HashMap<>();
        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM product WHERE status = 1");
            boolean hasCat = categoryId != null && !categoryId.isEmpty();
            if (hasCat) countSql.append(" AND category_id = ?");
            try (PreparedStatement psCount = conn.prepareStatement(countSql.toString())) {
                if (hasCat) psCount.setLong(1, Long.parseLong(categoryId));
                try (ResultSet rsCount = psCount.executeQuery()) {
                    int totalCount = rsCount.next() ? rsCount.getInt(1) : 0;
                    data.put("totalCount", totalCount);
                    data.put("totalPages", (int) Math.ceil((double) totalCount / pageSize));
                }
            }

            String orderClause = "sales DESC";
            if (sort != null) {
                switch (sort) {
                    case "price_asc": orderClause = "price ASC"; break;
                    case "price_desc": orderClause = "price DESC"; break;
                    case "newest": orderClause = "publish_time DESC"; break;
                }
            }

            StringBuilder sql = new StringBuilder("SELECT p.*, s.shop_name FROM product p LEFT JOIN shop s ON p.shop_id = s.id WHERE p.status = 1");
            if (hasCat) sql.append(" AND p.category_id = ?");
            sql.append(" ORDER BY p.").append(orderClause).append(" LIMIT ?, ?");
            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                int idx = 1;
                if (hasCat) ps.setLong(idx++, Long.parseLong(categoryId));
                ps.setInt(idx++, (page - 1) * pageSize);
                ps.setInt(idx++, pageSize);
                List<Map<String, Object>> products = new ArrayList<>();
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> p = new HashMap<>();
                        p.put("id", rs.getLong("id"));
                        p.put("name", rs.getString("name"));
                        p.put("price", rs.getString("price"));
                        p.put("coverImage", rs.getString("cover_image"));
                        p.put("sales", rs.getInt("sales"));
                        p.put("shopName", rs.getString("shop_name"));
                        products.add(p);
                    }
                }
                data.put("products", products);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询商品列表失败", e);
        }
        return data;
    }

    public Map<String, Object> searchProducts(String keyword, String sort, int page, int pageSize) {
        Map<String, Object> data = new HashMap<>();
        try (Connection conn = DBUtil.getConnection()) {
            String countSql = "SELECT COUNT(*) FROM product p LEFT JOIN category c ON p.category_id = c.id " +
                    "LEFT JOIN category pc ON c.parent_id = pc.id " +
                    "WHERE p.status = 1 AND (p.name LIKE ? OR p.description LIKE ? OR c.name LIKE ? OR pc.name LIKE ?)";
            try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
                String kw = "%" + keyword + "%";
                psCount.setString(1, kw);
                psCount.setString(2, kw);
                psCount.setString(3, kw);
                psCount.setString(4, kw);
                try (ResultSet rsCount = psCount.executeQuery()) {
                    int totalCount = rsCount.next() ? rsCount.getInt(1) : 0;
                    data.put("totalCount", totalCount);
                    data.put("totalPages", (int) Math.ceil((double) totalCount / pageSize));
                }
            }

            String orderClause = "sales DESC";
            if (sort != null) {
                switch (sort) {
                    case "price_asc": orderClause = "price ASC"; break;
                    case "price_desc": orderClause = "price DESC"; break;
                    case "newest": orderClause = "publish_time DESC"; break;
                }
            }

            String sql = "SELECT p.*, s.shop_name FROM product p LEFT JOIN shop s ON p.shop_id = s.id " +
                    "LEFT JOIN category c ON p.category_id = c.id " +
                    "LEFT JOIN category pc ON c.parent_id = pc.id " +
                    "WHERE p.status = 1 AND (p.name LIKE ? OR p.description LIKE ? OR c.name LIKE ? OR pc.name LIKE ?) " +
                    "ORDER BY p." + orderClause + " LIMIT ?, ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String kw = "%" + keyword + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
                ps.setString(3, kw);
                ps.setString(4, kw);
                ps.setInt(5, (page - 1) * pageSize);
                ps.setInt(6, pageSize);
                List<Map<String, Object>> products = new ArrayList<>();
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> p = new HashMap<>();
                        p.put("id", rs.getLong("id"));
                        p.put("name", rs.getString("name"));
                        p.put("price", rs.getString("price"));
                        p.put("coverImage", rs.getString("cover_image"));
                        p.put("sales", rs.getInt("sales"));
                        p.put("shopName", rs.getString("shop_name"));
                        products.add(p);
                    }
                }
                data.put("products", products);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("搜索商品失败", e);
        }
        return data;
    }

    // 商家查询商品列表（含分类名），按 id 倒序
    public List<Map<String, Object>> listShopProducts(long shopId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS cat_name FROM product p " +
                     "LEFT JOIN category c ON p.category_id = c.id " +
                     "WHERE p.shop_id = ? ORDER BY p.id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> p = new HashMap<>();
                    p.put("id", rs.getLong("id"));
                    p.put("name", rs.getString("name"));
                    p.put("cat_name", rs.getString("cat_name"));
                    p.put("price", rs.getString("price"));
                    p.put("stock", rs.getInt("stock"));
                    p.put("sales", rs.getInt("sales"));
                    p.put("status", rs.getInt("status"));
                    p.put("cover_image", rs.getString("cover_image"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询商家商品列表失败", e);
        }
        return list;
    }

    // 商家查询商品详情（用于编辑，校验 shopId 归属防止越权）
    public Map<String, Object> getShopProductById(long id, long shopId) {
        String sql = "SELECT * FROM product WHERE id = ? AND shop_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> p = new HashMap<>();
                    p.put("id", rs.getLong("id"));
                    p.put("name", rs.getString("name"));
                    p.put("description", rs.getString("description"));
                    p.put("price", rs.getString("price"));
                    p.put("stock", rs.getInt("stock"));
                    p.put("category_id", rs.getLong("category_id"));
                    p.put("images", rs.getString("images"));
                    p.put("cover_image", rs.getString("cover_image"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询商家商品详情失败", e);
        }
        return null;
    }

    // 商家更新商品（动态SQL：coverImage/imagesJson 为空时不更新，WHERE 条件附加 shop_id 校验归属防止越权）
    public boolean saveShopProduct(long id, long shopId, String name, String description, double price,
                                   int stock, long categoryId, String coverImage, String imagesJson) {
        StringBuilder sql = new StringBuilder("UPDATE product SET name=?, description=?, price=?, stock=?, category_id=?");
        if (coverImage != null && !coverImage.isEmpty()) sql.append(", cover_image=?");
        if (imagesJson != null && !imagesJson.isEmpty()) sql.append(", images=?");
        sql.append(" WHERE id=? AND shop_id=?");
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setString(idx++, name);
            ps.setString(idx++, description);
            ps.setDouble(idx++, price);
            ps.setInt(idx++, stock);
            ps.setLong(idx++, categoryId);
            if (coverImage != null && !coverImage.isEmpty()) ps.setString(idx++, coverImage);
            if (imagesJson != null && !imagesJson.isEmpty()) ps.setString(idx++, imagesJson);
            ps.setLong(idx++, id);
            ps.setLong(idx, shopId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("商家更新商品失败", e);
        }
    }

    // 商家新增商品（status=1, publish_time=NOW()），返回新ID
    public long saveShopProduct(long shopId, long categoryId, String name, String description,
                                double price, int stock, String coverImage, String imagesJson) {
        String sql = "INSERT INTO product (shop_id, category_id, name, description, price, stock, cover_image, images, status, publish_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1, NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, shopId);
            ps.setLong(2, categoryId);
            ps.setString(3, name);
            ps.setString(4, description);
            ps.setDouble(5, price);
            ps.setInt(6, stock);
            ps.setString(7, coverImage);
            ps.setString(8, imagesJson);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("商家新增商品失败", e);
        }
        return -1;
    }

    // 更新商品状态（附加 shop_id 校验归属防止越权）
    public boolean updateProductStatus(long id, int status, long shopId) {
        String sql = "UPDATE product SET status = ? WHERE id = ? AND shop_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setLong(2, id);
            ps.setLong(3, shopId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新商品状态失败", e);
        }
    }

    public Map<String, Object> getProductDetail(long id) {
        Map<String, Object> data = null;
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT p.*, s.shop_name, s.id AS shop_id FROM product p LEFT JOIN shop s ON p.shop_id = s.id WHERE p.id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data = new HashMap<>();
                        data.put("id", rs.getLong("id"));
                        data.put("name", rs.getString("name"));
                        data.put("description", rs.getString("description"));
                        data.put("price", rs.getString("price"));
                        data.put("stock", rs.getInt("stock"));
                        data.put("sales", rs.getInt("sales"));
                        data.put("images", rs.getString("images"));
                        data.put("coverImage", rs.getString("cover_image"));
                        data.put("shopName", rs.getString("shop_name"));
                    }
                }
            }

            if (data != null) {
                String reviewSql = "SELECT r.*, u.nickname FROM review r LEFT JOIN user u ON r.user_id = u.id WHERE r.product_id = ? AND r.status = 1 ORDER BY r.create_time DESC LIMIT 10";
                List<Map<String, Object>> reviews = new ArrayList<>();
                try (PreparedStatement ps2 = conn.prepareStatement(reviewSql)) {
                    ps2.setLong(1, id);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        while (rs2.next()) {
                            Map<String, Object> r = new HashMap<>();
                            r.put("nickname", rs2.getString("nickname"));
                            r.put("rating", rs2.getInt("rating"));
                            r.put("content", rs2.getString("content"));
                            r.put("reply", rs2.getString("reply"));
                            r.put("createTime", rs2.getTimestamp("create_time"));
                            reviews.add(r);
                        }
                    }
                }
                data.put("reviews", reviews);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询商品详情失败", e);
        }
        return data;
    }
}
