package com.ald.jsd.mgr.spring;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ald.jsd.mgr.enums.RespCode;
import com.ald.jsd.mgr.web.Sessions;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.alibaba.fastjson.JSON;

public class LoginInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            if (hm.getMethod().getAnnotation(NotNeedLogin.class) != null 
             || hm.getBeanType().getAnnotation(NotNeedLogin.class) != null) {
            }else {
            	if(!Sessions.isLogin(request)) {
            		Resp<?> resp = Resp.fail(null, RespCode.LOGIN_EXPIRE.code, RespCode.LOGIN_EXPIRE.desc);
            		render(JSON.toJSONString(resp), response);
                	return false;
            	}
            }
        }
        return true;
		
	}
	
	public void render(String text, HttpServletResponse res) throws IOException{
		try {
			res.getWriter().write(text);
			res.getWriter().flush();
		}finally {
			try {
				res.getWriter().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
