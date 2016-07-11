package com.pabula.fw.utility;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
