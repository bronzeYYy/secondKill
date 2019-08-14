package cn.chen.second.controller;

import cn.chen.second.exception.IllegalStateException;
import cn.chen.second.service.GoodsService;
import cn.chen.second.service.impl.GoodsServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "KillServlet", urlPatterns = "/kill")
public class KillServlet extends HttpServlet {
    private final GoodsService goodsService = GoodsServiceImpl.getInstance();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        request.getParameter("id");
        Cookie[] cookies = request.getCookies();
        String phone = null;
        for (Cookie cookie : cookies) {
            if ("phone".equals(cookie.getName())) {
                phone = cookie.getValue();
                break;
            }
        }
        if (phone == null) {
            // 未登陆
            response.sendRedirect("/login");
            return;
        }
        String id = request.getParameter("id");
        int goodsId;

        PrintWriter writer = response.getWriter();
        try {
            goodsId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            writer.write("商品信息有误");
            return;
        }
        try {
            if (goodsService.killGoods(goodsId, phone)) {
                // 秒杀成功
                writer.write("成功秒杀");
            } else {
                writer.write("秒杀失败");
            }
        } catch (IllegalStateException e) {
            writer.write(e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(405);
    }
}
