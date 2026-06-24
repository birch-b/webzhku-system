package com.taobao.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * 全局字符编码过滤器
 * 统一设置请求和响应的字符编码为UTF-8
 * 在web.xml中配置
 */
public class EncodingFilter implements Filter {

    private static final String ENCODING = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding(ENCODING);
        response.setCharacterEncoding(ENCODING);
        response.setContentType("text/html;charset=" + ENCODING);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
