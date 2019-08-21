package cn.chen.second.controller;

import cn.chen.second.model.Goods;
import cn.chen.second.service.impl.GoodsServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "IndexServlet", urlPatterns = {"", "/index"})
public class IndexServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(405);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if ("phone".equals(cookie.getName())) {
                request.setAttribute("phone", cookie.getValue());
                break;
            }
        }
        if (request.getAttribute("phone") == null) {
            response.sendRedirect("/login");
            return;
        }
        // todo 从数获取秒杀商品
        List<Goods> goods = GoodsServiceImpl.getInstance().getAllGoods();
        request.setAttribute("goods", goods);
        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
        // todo 重定向到主页
    }
}
