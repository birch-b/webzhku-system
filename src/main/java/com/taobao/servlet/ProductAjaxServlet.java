package com.taobao.servlet;

import com.taobao.service.ProductService;
import com.taobao.service.impl.ProductServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/product/ajax")
public class ProductAjaxServlet extends HttpServlet {
    private final ProductService productService = new ProductServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        String categoryName = req.getParameter("category");

        List<Map<String, Object>> products;
        try {
            // 只调用业务层，无任何SQL、JDBC代码
            products = productService.listProductByCategory(categoryName);
        } catch (RuntimeException e) {
            e.printStackTrace();
            // 异常返回空数组
            resp.getWriter().write("[]");
            return;
        }
        // 序列化为JSON输出
        String json = objectMapper.writeValueAsString(products);
        resp.getWriter().write(json);
    }
}