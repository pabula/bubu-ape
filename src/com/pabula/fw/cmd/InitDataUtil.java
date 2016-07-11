package com.pabula.fw.cmd;

import com.pabula.common.util.DateUtil;
import com.pabula.common.util.SeqNumHelper;
import com.pabula.common.util.StrUtil;
import com.pabula.fw.exception.DataAccessException;
import com.pabula.fw.utility.RequestData;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 初始化数据工具类
 * Created by pabula on 16/7/1.
 */
public class InitDataUtil {



    /**
     * 根据提供的值（可能是带变量的），将值中的变量替换后返回具体的值
     * @param ruleStr
     * @return
     */
    public  String getValueByRuleStr(RequestData requestData,String ruleStr){
        String value = ruleStr;
        if(ruleStr.startsWith("$")){

            /****************************
             * 序列号处理
             * $seq('db_source','table_name','$session.service_id')
             * $seq('数据源','表','业务域')
             ***************************/
            if(ruleStr.startsWith("$seq")){

                String dbSource = "";   //数据源: 比如 java
                String type1 = "";  //表格：比如 product
                String type2 = "";   //业务：比如service_id

                //使用正则，匹配seq('xxx')这样的，得到seq的参数
                Pattern pat = Pattern.compile("\'(.*?)\'");     //正则匹配： http://zhidao.baidu.com/question/337557426.html
                Matcher mat = pat.matcher(ruleStr);
                if(mat.find()){
                    dbSource = mat.group(0);
                    type1 = mat.group(1);
                    type2 = mat.group(2);
                }

                //生成序列号
                if(StrUtil.isNotNull(dbSource) && StrUtil.isNotNull(type1)){
                    try {
                        //三个参数中也有可能带变量，所以，再处理一次变量,换成对应的值
                        dbSource = getValueByFiled(requestData,ruleStr);
                        type1 = getValueByFiled(requestData,type1);
                        type2 = getValueByFiled(requestData,type2);

                        //根据table生成序列号
                        value = String.valueOf(SeqNumHelper.getNewSeqNum(dbSource, type1, type2));
                    } catch (DataAccessException e) {
                        e.printStackTrace();
                    }
                }

                /****************************
                 * 日期处理
                 * $datetime('yyyy-mm-dd hh:mm:ss')
                 * $datetime('日期格式（同JAVA）')
                 ***************************/
            }else if(ruleStr.startsWith("$datetime")){  //时间的处理
                String dateFormat = "yyyy-MM-dd hh:mm:ss";
                //使用正则，匹配 $datetime('yyyy-mm-dd hh:mm:ss') 这样的，得到参数
                Pattern pat = Pattern.compile("\'(.*?)\'");     //正则匹配： http://zhidao.baidu.com/question/337557426.html
                Matcher mat = pat.matcher(ruleStr);
                if(mat.find()){
                    dateFormat = mat.group(0);
                }

                //生成日期
                value = DateUtil.getCurrentDay(dateFormat);
            }

            /****************************
             * 其它，即$data\$session\$cookie
             ***************************/
        }else{
            value = getAllFiledValue(requestData,ruleStr);
        }
        return value;
    }

    /**
     * 根据提供的filed，得到对应的值
     * 包括以下格式
     * $session.service_id
     * $data.unit
     * $cookie.abc
     * @param requestData
     * @param filed
     * @return
     */
    private String getValueByFiled(RequestData requestData,String filed){
        String value = "";

        HashMap data = getDataMapByFiled(requestData, filed);   //得到$session.service_id中$session的hashmap
        String key = getFiledKey(filed);    //得到$session.service_id中的service_id

        if(data.containsKey(key)){
            value = (String)data.get(key);  //得到具体的值
        }

        return value;
    }

    /**
     * 根据filed，得到其中的key值
     * 例如 $session.service_id 得到 service_id；$data.unit得到unit；
     * @param filed
     * @return
     */
    private String getFiledKey(String filed){
        String key = filed.toLowerCase().trim();

        if(filed.startsWith("$")){
            key = StrUtil.replaceAll(key,"$DATA.","");
            key = StrUtil.replaceAll(key,"$SESSION.","");
            key = StrUtil.replaceAll(key,"$COOKIE.","");
        }

        return key;
    }

    /**
     * 根据提供的filed，确定这个是在哪个数据集中（request的data、session、cookie）
     * @param requestData
     * @param filed
     * @return
     */
    private HashMap getDataMapByFiled(RequestData requestData,String filed){
        filed = filed.toUpperCase().trim(); //转为大写，并去除前后空格
        HashMap checkObjMap = requestData.getData();
        if(filed.startsWith("$SESSION.")){
            checkObjMap = requestData.getSession();
        } else if(filed.startsWith("$COOKIE.")){
            checkObjMap = requestData.getCookie();
        }

        return checkObjMap;
    }

    /**
     * 根据提供的filed，将一个值设置在数据集中（request的data、session、cookie）
     * @param requestData
     * @param filed
     * @param value
     */
    private void putDataMapByFiled(RequestData requestData,String filed,String value){
        if(filed.startsWith("$SESSION.")){
            requestData.getSession().put(filed,value);
        } else if(filed.startsWith("$COOKIE.")){
            requestData.getCookie().put(filed,value);
        }else{
            requestData.getData().put(filed,value);
        }
    }

    /**
     * 根据提供的str，替换掉所有的变量，得到最终的字符串
     * @param requestData
     * @param str
     * @return
     */
    private String getAllFiledValue(RequestData requestData,String str){
        String value = str;

        //使用正则，匹配 $datetime('yyyy-mm-dd hh:mm:ss') 这样的，得到参数
        Pattern pat = Pattern.compile("\\{(.*?)\\}");     //正则匹配： {}
        Matcher mat = pat.matcher(value);

        for(int i = 1;i<=mat.groupCount();i++){
            String ruleStr = mat.group(i);  //匹配{}中间的内容
            String ruleValue = getValueByFiled(requestData,ruleStr);    //将变量替换为值
            StrUtil.replaceAll(value,ruleStr,ruleValue);    //替换掉原文中的变量为值
        }

        return value;
    }

    /**
     * 根据提供的filed，确定这个是在哪个数据集中（request的data、session、cookie）
     * @param requestData
     * @param filed
     * @return
     */
    private HashMap getDataMapByFiled(RequestData requestData,String filed){
        filed = filed.toUpperCase().trim(); //转为大写，并去除前后空格
        HashMap checkObjMap = requestData.getData();
        if(filed.startsWith("$SESSION.")){
            checkObjMap = requestData.getSession();
        } else if(filed.startsWith("$COOKIE.")){
            checkObjMap = requestData.getCookie();
        }

        return checkObjMap;
    }


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
