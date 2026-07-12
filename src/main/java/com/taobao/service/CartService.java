package com.taobao.service;

import java.util.List;
import java.util.Map;

/**
 * 购物车业务逻辑层接口（买家端）
 * 纯业务方法：入参简单类型/主键ID，返回值 DTO/List/void
 * 绝对不碰 HttpServletRequest / HttpServletResponse / redirect
 * 错误全部上抛 RuntimeException（Servlet 捕获后跳错误页）
 */
public interface CartService {

    /**
     * 查询指定用户的购物车
     * @param userId  用户ID（session里取的）
     * @return map 两个键：
     *         "cartItems"   → List<String[]>  和原 CartServlet 格式完全一致，cart.jsp 0 改动
     *                          [0]cart_item.id [1]product_id [2]name [3]price [4]quantity
     *                          [5]quantity*price [6]cover_image [7]selected [8]stock [9]product_status
     *         "totalAmount" → double 已勾选商品总金额
     */
    Map<String, Object> listCart(Long userId);

    /**
     * 添加商品到购物车（存在则加数量，不存在则insert）
     * @param userId      买家用户ID
     * @param productId   商品ID
     * @param quantity    数量（调用方传入默认1；非法则默认1，超库存则截短到库存）
     * @throws RuntimeException 商品已下架/数据库异常
     */
    void addToCart(Long userId, Long productId, int quantity);

    /**
     * 更新购物车单项数量（qty <= 0 则删除该项）
     */
    void updateCart(Long cartId, int quantity);

    /**
     * 删除购物车单项
     */
    void deleteCart(Long cartId);

    /**
     * 全选 / 全不选（按用户ID批量 UPDATE）
     * @param selected 1 全选 / 0 全不选
     */
    void selectAll(Long userId, int selected);

    /**
     * 切换单个购物车项的选中状态（IF(selected=1,0,1)）
     */
    void toggleSelect(Long cartId);
}
