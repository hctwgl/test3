package com.ald.fanbei.api.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.ald.fanbei.api.common.util.Base64;



/**
 * 
 * 类CookieUtil.java的实现描述：CookieUtil
 * @author 陈金虎 2015年12月18日 下午2:35:29
 */
public class CookieUtil {

    /**
     * <p>
     * 根据name获取对应的cookie，该name下的cookie存在，则返回，否则返回null;
     * </p>
     * 
     * <pre>
     * for example:
     * getCookie(request,"")=null;
     * getCookie(request,null)=null;
     * getCookie(request,"aa")=key为aa的cookie
     * </pre>
     * 
     * @param request
     * @param name
     * @return javax.servlet.http.Cookie
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie == null) {
                continue;
            }
            if (StringUtils.equals(cookie.getName(), name)) {
                return cookie;
            }
        }
        return null;
    }

    /**
     * 写COOKEI
     * 
     * @param response
     * @param name
     * @param value
     */
    public static void writeCookie(HttpServletResponse response, String name, String value, int expiry) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(expiry);
        response.addCookie(cookie);
    }
    
    /**
     * 写COOKEI
     * 
     * @param response
     * @param name
     * @param value
     */
    public static void writeCookieEncode(HttpServletResponse response, String name, String value, int expiry) {
        Cookie cookie;
        try {
            cookie = new Cookie(name, URLEncoder.encode(value,"UTF-8"));
            cookie.setPath("/");
            cookie.setMaxAge(expiry);
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除全部cookie
     * 
     * @param request
     * @param response
     * @param name
     */
    public static void removeAllCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            removeCookie(cookies[i].getName(), response);
        }
    }

    /**
     * 清除指定cookie
     * 
     * @param key
     * @param response
     */
    public static void removeCookie(String key, HttpServletResponse response) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    /**
     * decode cookie value
     * 
     * @param request
     * @param key
     * @return
     * @throws Exception
     */
    public static String decodeCookieVal(HttpServletRequest request, String key) throws Exception {
        Cookie cookie = CookieUtil.getCookie(request, key);
        if (null == cookie || StringUtils.isBlank(cookie.getValue())) {
            return StringUtils.EMPTY;
        }
        byte[] bytes = DesedeUtil.decryptEdeDES(Constants.TIRPLE_DES_KEY.getBytes(),
                                                Base64.decode(cookie.getValue()));
        return StringUtils.trim(new String(bytes));
    }

}
