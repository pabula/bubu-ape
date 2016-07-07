package com.pabula.fw.cmd;

import com.pabula.common.util.StrUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * 初始化数据工具类
 * Created by pabula on 16/7/1.
 */
public class InitDataUtil {


    /**
     * 将request中所有param的名称和值，填充在hashmap中
     * @param request
     * @return
     */
    public static HashMap getRequestData(HttpServletRequest request){
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
    public static HashMap getSessionData(HttpServletRequest request){
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
    public static HashMap getCookieData(HttpServletRequest request){
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


}
