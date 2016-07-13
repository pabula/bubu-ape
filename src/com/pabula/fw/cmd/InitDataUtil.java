package com.pabula.fw.cmd;

import com.pabula.common.util.DateUtil;
import com.pabula.common.util.SeqNumHelper;
import com.pabula.common.util.StrUtil;
import com.pabula.fw.exception.DataAccessException;
import com.pabula.fw.utility.RequestData;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 初始化数据工具类
 * Created by pabula on 16/7/1.
 */
public class InitDataUtil {




    /**
     * 根据提供的filed，将一个值设置在数据集中（request的data、session、cookie）
     * @param requestData
     * @param filed
     * @param value
     */
    public static void putDataMapByFiled(RequestData requestData,String filed,String value){
        filed = filed.toUpperCase();
        if(filed.startsWith("$SESSION.")){
            requestData.getSession().put(filed,value);
        } else if(filed.startsWith("$COOKIE.")){
            requestData.getCookie().put(filed,value);
        }else{
            requestData.getData().put(filed,value);
        }
    }


    /**
     * 获得一个变量字符串对应的值(包括了字符串中的 变量 与 方法)
     * @param requestData
     * @param varString
     * @return
     */
    public static String getValue(RequestData requestData,String varString){

        String value = varString;


        /****************************
         * 1.替换掉 变量
         ***************************/
        value = getValueByVar(requestData,varString);

        /****************************
         * 2.替换掉 函数
         ***************************/
        value = getValueByFunction(requestData,value);


        return value;

    }



    /**
     * 根据提供的str，替换掉所有的变量，得到最终的字符串
     * @param requestData
     * @param varString
     * @return
     */
    public static String getValueByVar(RequestData requestData,String varString){
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

            String varValue = findVarName;    //将变量替换为值

            HashMap dataMap = getDataMapByFiled(requestData, findVarName);   //得到$session.service_id中$session的hashmap
            String key = getVarKey(findVarName);    //得到$session.service_id中的service_id

            if(dataMap.containsKey(key)){
                varValue = (String)dataMap.get(key);  //得到具体的值
            }

            StrUtil.replaceAll(value,findVar,varValue);    //替换掉原文中的变量为值
        }

        return value;
    }

    /**
     * 根据提供的值（可能是带变量的），将值中的变量替换后返回具体的值
     * @param functionStr
     * @return
     */
    public  static String getValueByFunction(RequestData requestData, String functionStr){

        String value = functionStr;

        if(functionStr.startsWith("$")){

            /****************************
             * 序列号处理
             * $seq('db_source','table_name','$session.service_id')
             * $seq('数据源','表','业务域')
             ***************************/
            if(functionStr.startsWith("$seq")){

                String dbSource = "";   //数据源: 比如 java
                String type1 = "";  //表格：比如 product
                String type2 = "";   //业务：比如service_id

                //使用正则，匹配seq('xxx')这样的，得到seq的参数
                Pattern pat = Pattern.compile("\'(.*?)\'");     //正则匹配： http://zhidao.baidu.com/question/337557426.html
                Matcher mat = pat.matcher(functionStr);

                int paramIndex = 0;
                while (mat.find()){
                    paramIndex ++;
                    if(paramIndex == 1){
                        dbSource = mat.group();
                        dbSource = dbSource.substring(1,dbSource.length()-1);   //去掉''
                    }else if(paramIndex == 2){
                        type1 = mat.group();
                        type1 = type1.substring(1,type1.length()-1);    //去掉''
                    }else if(paramIndex == 3){
                        type2 = mat.group();
                        type2 = type2.substring(1,type2.length()-1);    //去掉''
                    }
                }




                //生成序列号
                if(StrUtil.isNotNull(dbSource) && StrUtil.isNotNull(type1)){
                    try {
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
            }else if(functionStr.startsWith("$datetime")){  //时间的处理
                String dateFormat = "yyyy-MM-dd hh:mm:ss";
                //使用正则，匹配 $datetime('yyyy-mm-dd hh:mm:ss') 这样的，得到参数
                Pattern pat = Pattern.compile("\'(.*?)\'");     //正则匹配： http://zhidao.baidu.com/question/337557426.html
                Matcher mat = pat.matcher(functionStr);
                if(mat.find()){
                    dateFormat = mat.group();
                }

                //去掉filed前后的''
                dateFormat= dateFormat.substring(1,dateFormat.length()-1);

                //生成日期
                value = DateUtil.getCurrentDay(dateFormat);
            }

            /****************************
             * 其它，即$data\$session\$cookie
             ***************************/
        }


        return value;
    }


//
//
//    /**
//     * 根据提供的filed，得到对应的值
//     * 包括以下格式
//     * $session.service_id
//     * $data.unit
//     * $cookie.abc
//     * @param requestData
//     * @param var
//     * @return
//     */
//    public static String getValueByVar(RequestData requestData,String var){
//        String value = "";
//
//        HashMap dataMap = getDataMapByFiled(requestData, var);   //得到$session.service_id中$session的hashmap
//        String key = getVarKey(var);    //得到$session.service_id中的service_id
//
//        if(dataMap.containsKey(key)){
//            value = (String)dataMap.get(key);  //得到具体的值
//        }
//
//        return value;
//    }








    /**
     * 根据提供的filed，确定这个是在哪个数据集中（request的data、session、cookie）
     * @param requestData
     * @param filed
     * @return
     */
    public static HashMap getDataMapByFiled(RequestData requestData,String filed){
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
     * 根据filed，得到其中的key.  例如 $session.service_id 得到 service_id；$data.unit得到unit；
     * @param var
     * @return
     */
    private static String getVarKey(String var){
        String key = var.toUpperCase().trim();

        if(var.startsWith("$")){
            key = StrUtil.replaceAll(key,"$DATA.","");
            key = StrUtil.replaceAll(key,"$SESSION.","");
            key = StrUtil.replaceAll(key,"$COOKIE.","");
        }

        return key;
    }


    public static void main(String[] args) throws Exception {
        String value = "aaa.{$data.unit.id}.bbb.{$session.id}";

        //使用正则，匹配 $datetime('yyyy-mm-dd hh:mm:ss') 这样的，得到参数
        Pattern pat = Pattern.compile("\\{(.*?)\\}");     //正则匹配： {}
        Matcher mat = pat.matcher(value);

        while(mat.find()){
            System.err.println("group: " + mat.group());
            System.err.println("begin: " + value);
            value = value.replaceAll("\\{(.*?)\\}","111");
        }

        System.err.println("end: " + value);
    }




}
