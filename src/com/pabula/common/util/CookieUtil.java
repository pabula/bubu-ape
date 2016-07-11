package com.pabula.common.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie������
 * Created by Pabula on 2015/7/7.
 */
public class CookieUtil {

    /**
     * ����cookie��Ч�ڣ�������Ҫ�Զ���[��ϵͳ����Ϊ30��]
     */
    private final static int COOKIE_MAX_AGE = 1000 * 60 * 60 * 24 * 30;

    /**
     *
     * @desc ɾ��ָ��Cookie
     * @param response
     * @param cookie
     */
    public static void removeCookie(HttpServletResponse response, Cookie cookie){
        if (cookie != null){
            cookie.setPath("/");
            cookie.setValue("");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    /**
     *
     * @desc ɾ��ָ��Cookie
     * @param response
     * @param cookie
     * @param domain
     */
    public static void removeCookie(HttpServletResponse response, Cookie cookie,String domain){
        if (cookie != null){
            cookie.setPath("/");
            cookie.setValue("");
            cookie.setMaxAge(0);
            cookie.setDomain(domain);
            response.addCookie(cookie);
        }
    }

    /**
     *
     * @desc ����Cookie���Ƶõ�Cookie��ֵ��û�з���Null
     * @param request
     * @param name
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String name){
        Cookie cookie = getCookie(request, name);
        if (cookie != null){
            return cookie.getValue();
        }else{
            return null;
        }
    }

    /**
     *
     * @desc ����Cookie���Ƶõ�Cookie���󣬲����ڸö����򷵻�Null
     * @param request
     * @param name
     */
    public static Cookie getCookie(HttpServletRequest request, String name){
        Cookie cookies[] = request.getCookies();
        if (cookies == null || name == null || name.length() == 0)
            return null;
        Cookie cookie = null;
        for (int i = 0; i < cookies.length; i++){
            if (!cookies[i].getName().equals(name))
                continue;
            cookie = cookies[i];
            if (request.getServerName().equals(cookie.getDomain()))
                break;
        }

        return cookie;
    }

    /**
     *
     * @desc ���һ���µ�Cookie��Ϣ��Ĭ����Чʱ��Ϊһ����
     * @param response
     * @param name
     * @param value
     */
    public static void setCookie(HttpServletResponse response, String name, String value){
        setCookie(response, name, value, COOKIE_MAX_AGE);
    }

    /**
     *
     * @desc ���һ���µ�Cookie��Ϣ���������������Чʱ��(��λ����)
     * @param response
     * @param name
     * @param value
     * @param maxAge
     */
    public static void setCookie(HttpServletResponse response, String name, String value, int maxAge){
        if (value == null)
            value = "";
        Cookie cookie = new Cookie(name, value);
        if(maxAge!=0){
            cookie.setMaxAge(maxAge);
        }else{
            cookie.setMaxAge(COOKIE_MAX_AGE);
        }
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
