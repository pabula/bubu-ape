package com.pabula.fw.utility;

import com.pabula.common.util.StrUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * 将请求中的所有必要信息,放在这里
 * Created by Pabula on 16/6/27 22:27.
 */
public class RequestData {

    private HttpServletRequest request = null;
    private HttpServletResponse response = null;

    HashMap data = new HashMap();
    HashMap session = new HashMap();
    HashMap cookie = new HashMap();

    //返回值
    String returnValue = "";


    public RequestData(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        //获得并设置 request\cookie\session 的值
        this.setData(this.getRequestData(request));
        this.setCookie(this.getCookieData(request));
        this.setSession(this.getSessionData(request));
    }


    /**
     * 将request中所有param的名称和值，填充在hashmap中
     * @param request
     * @return
     */
    private  HashMap getRequestData(HttpServletRequest request){
        HashMap map = new HashMap();
        Enumeration names = request.getParameterNames();

        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();

            String[] value = request.getParameterValues(name);
            String overValue = "";

            if (value.length > 0) {
                int i = 0;

                while (i < value.length) {
                    if (StrUtil.isNotNull(value[i])) {
                        if (overValue.equals("")) {
                            overValue = value[i];
                        } else {
                            overValue = overValue + "," + value[i];
                        }
                    }
                    i++;
                }

                if (StrUtil.isNotNull(overValue)) {
                    map.put(name, overValue);
                }
            }
        }

        return map;
    }

    /**
     * 枚举session中的attribute，并放在hashmap中
     * @param request
     * @return
     */
    private  HashMap getSessionData(HttpServletRequest request){
        HashMap map = new HashMap();
        Enumeration names = request.getSession().getAttributeNames();

        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();

            Object obj = request.getSession().getAttribute(name);
            if(obj instanceof String){  //如果是字符型的
                String value = (String)obj;
                if (StrUtil.isNotNull(value)) {
                    map.put(name, value);
                }
            }
        }

        return map;
    }


    /**
     * 枚举cookie中的信息，并放在hashmap中
     * @param request
     * @return
     */
    private  HashMap getCookieData(HttpServletRequest request){
        HashMap map = new HashMap();
        Cookie[] cookies = request.getCookies();

        if(cookies!=null){
            for(int i = 0;i<cookies.length;i++){
                Cookie c = cookies[i];
                if(StrUtil.isNotNull(c.getName())){
                    map.put(c.getName(),c.getValue());
                }
            }
        }

        return map;
    }


    public HttpServletRequest getRequest() {
        return this.request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public HashMap getData() {
        return data;
    }

    public void setData(HashMap data) {
        this.data = data;
    }

    public HashMap getSession() {
        return session;
    }

    public void setSession(HashMap session) {
        this.session = session;
    }

    public HashMap getCookie() {
        return cookie;
    }

    public void setCookie(HashMap cookie) {
        this.cookie = cookie;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }
}
