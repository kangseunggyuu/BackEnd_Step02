package org.zerock.w2.controller;

import lombok.extern.log4j.Log4j2;
import org.zerock.w2.dto.MemberDTO;
import org.zerock.w2.service.MemberService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/login")
@Log4j2
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("login get..........");

        req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("login post..........");

        String mid = req.getParameter("mid");
        String mpw = req.getParameter("mpw");

        // UUID로 자동로그인 할 것인지 여부
        String auto = req.getParameter("auto");

        // login.jsp에 name=auto가 check되어 있다면, rememberMe는 true
        boolean rememberMe = auto != null && auto.equals("on");


        try{
            // 기존 로그인
            // id/pwd가 일치할 경우 memberDTO를 session공간에 저장한다.
            MemberDTO memberDTO = MemberService.INSTANCE.login(mid, mpw);

            // 자동로그인 설정이 되어 있는 경우
            if(rememberMe) {
                // 유니크한 문자열을 생성할 때 사용
                // ex) ba65870f-9b4f-4146-9cd8-58bd11baf5de
                String uuid = UUID.randomUUID().toString();

                // Maria-DB에 uuid를 저장
                MemberService.INSTANCE.updateUuid(mid, uuid);
                // loginInfo로 사용할 memberDTO에 uuid저장
                memberDTO.setUuid(uuid);

                // remember-me라는 이름의 쿠키를 새로 생성
                Cookie rememberCookie = new Cookie("remember-me", uuid);
                rememberCookie.setMaxAge(60*60*24*7);       // 7일 유효기간
                rememberCookie.setPath("/");

                // 브라우저에 응답보낼 때 remember-me를 전송해라
                resp.addCookie(rememberCookie);
            }

            // 세션공간에 memberDTO를 loginInfo라는 key값으로 저장
            HttpSession session = req.getSession();
            session.setAttribute("loginInfo", memberDTO);
            resp.sendRedirect("/todo/list");
        }catch(Exception e){
            resp.sendRedirect("/login?result=error");
        }

        /*
        String str = mid + mpw;
        HttpSession session = req.getSession();;
        // 임시도 id+pw의 문자열을 loginInfo로 session에 저장한다.
        session.setAttribute("loginInfo", str);
        resp.sendRedirect("/todo/list");
        */
    }
}
