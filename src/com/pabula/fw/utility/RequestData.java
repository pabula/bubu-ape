package com.pabula.fw.utility;

import com.pabula.common.util.StrUtil;
import com.pabula.fw.cmd.FunctionParseUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    map.put(name.toUpperCase(), overValue); //KEY全部以大写方式 存储
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
                    map.put(name.toUpperCase(), value);   //KEY全部以大写方式 存储
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
                    map.put(c.getName().toUpperCase(),c.getValue());  //KEY全部以大写方式 存储
                }
            }
        }

        return map;
    }






    /**
     * 获得一个变量字符串对应的值(包括了字符串中的 变量 与 方法)
     * @param varString
     * @return
     */
    public  String parseVar(String varString){

        String value;

        /****************************
         * 1.替换掉 变量
         ***************************/
        value = replaceVar(varString);

        /****************************
         * 2.替换掉 函数
         ***************************/
        value = FunctionParseUtil.parse(value);


        return value;

    }


    /**
     * 根据提供的str，替换掉所有的变量，得到最终的字符串
     * @param varString
     * @return
     */
    private String replaceVar(String varString){
        String value = varString;

        //使用正则，匹配 $datetime('yyyy-mm-dd hh:mm:ss') 这样的，得到参数
        Pattern pat = Pattern.compile("\\{(.*?)\\}");     //正则匹配： {}
        Matcher mat = pat.matcher(value);

        while (mat.find()){
            String findVar = mat.group();   //找到的变量字符串 {$data.id}

            String findVarName = findVar;
            //去掉filed前后的{}
            if(findVarName.startsWith("{")){
                findVarName = findVarName.substring(1);
            }
            if(findVarName.endsWith("}")){
                findVarName = findVarName.substring(0,findVarName.length()-1);
            }

            String varValue = StrUtil.getNotNullStringValue(getValueFromVar(findVarName),"");    //得到变量对应的值

            value = StrUtil.replaceAll(value,findVar,varValue);    //替换掉原文中的变量为值
        }

        return value;
    }


    /**
     * 根据提供的变量，设置对应变量集的值
     * @param var
     * @param value
     */
    public void setDataByKey(String var,String value){
        var = var.toUpperCase();

        String key = getVarKey(var);    //得到var的key，即变量名称
        value = parseVar(value);    //得到实际的值，其中可能有变量和方法

        if(var.startsWith("$SESSION.")){
            getSession().put(key,value);
        } else if(var.startsWith("$COOKIE.")){
            getCookie().put(key,value);
        }else{
            getData().put(key,value);
        }
    }



    /**
     * 根据变量名，得到值
     * @param var
     * @return
     */
    public String getValueFromVar(String var){
        String value;

        HashMap data = getDataMapByVar(var);  //确定变量在哪里（DATA\SESSION\COOKIE）
        String dataKey = getVarKey(var);    //得到变量的KEY值
        value = (String)data.get(dataKey);  //  从集合中，取得变量的值

        return value;
    }

    /**
     * 根据var得到对应的数据集合
     * @param var
     * @return
     */
    public HashMap getDataMapByVar(String var){
        var = var.toUpperCase().trim(); //转为大写，并去除前后空格

        HashMap checkObjMap = getData();
        if(var.startsWith("$SESSION.")){
            checkObjMap = getSession();
        } else if(var.startsWith("$COOKIE.")){
            checkObjMap = getCookie();
        }

        return checkObjMap;
    }


    /**
     * 根据filed，得到其中的key.  例如 $session.service_id 得到 service_id；$data.unit得到unit；
     * @param var
     * @return
     */
    private static String getVarKey(String var){
        String key = var.toUpperCase().trim();

        if(var.startsWith("$")){
            key = StrUtil.replaceAll(key,"$DATA.","");
            key = StrUtil.replaceAll(key,"$SESSION.","");
            key = StrUtil.replaceAll(key, "$COOKIE.", "");
        }

        return key;
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
