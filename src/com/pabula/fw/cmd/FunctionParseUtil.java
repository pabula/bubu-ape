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
 * function解析工具类
 * Created by pabula on 16/7/1.
 */
public class FunctionParseUtil {


    /**
     * 根据提供的值（可能是带变量的），将值中的变量替换后返回具体的值
     * @param functionStr
     * @return
     */
    public static String parse(String functionStr){

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
