package com.pabula.fw.cmd;

import com.alibaba.fastjson.JSONObject;
import com.pabula.common.util.DateUtil;
import com.pabula.common.util.SeqNumHelper;
import com.pabula.common.util.StrUtil;
import com.pabula.common.util.ValidateUtil;
import com.pabula.dao.CommonDAO;
import com.pabula.db.SqlHelper;
import com.pabula.fw.exception.DataAccessException;
import com.pabula.fw.exception.RuleException;
import com.pabula.fw.utility.Command;
import com.pabula.fw.utility.RequestData;
import com.sun.org.apache.xml.internal.security.Init;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用的COMMAND处理
 * Created by Pabula on 16/6/27 23:02.
 */
public abstract class CommonCommand implements Command{


    /**
     * 执行COMMAND业务逻辑
     * @return
     * @throws DataAccessException
     */
    public String main(RequestData requestData, Command command, JSONObject commandConfig,ValidateUtil validate) throws DataAccessException{

        /***************************************
         * 数据处理:通用处理+前切面+CMD实现+后切面
         **************************************/



        //先进行一次标准化的数据处理,再交给客户自己的command
        publicInitData(requestData,commandConfig);

        aspectFrontForInitdata(requestData);    //aspect: 前置切面
        initdata(requestData);  //CMD自己实现的初始数据
        aspectBackForInitdata(requestData); //aspect: 后置切面


        /***************************************
         * VALIDATE:通用处理+前切面+CMD实现+后切面
         **************************************/
        publicValidate(requestData,validate,commandConfig);

        aspectFrontForValidate(requestData, validate);   //aspect: 前置切面
        validate(requestData, validate); //CMD自己实现的validate
        aspectBackForValidate(requestData, validate);    //aspect: 后置切面


        /***************************************
         * 默认值:通用处理+前切面+CMD实现+后切面
         **************************************/


        /***************************************
         * EXECUTE:通用处理+前切面+CMD实现+后切面
         **************************************/
        String cmdReturnStr = "";
        try {
            publicExecute(requestData,requestData.getRequest(),commandConfig);    //不需要利用返回值

            aspectFrontForExecute(requestData, requestData.getRequest()); //aspect: 前置切面，因为这里不是最后一个执行方法，所以不需要返回值赋值
            cmdReturnStr = command.execute(requestData, requestData.getRequest());    //执行execute
            cmdReturnStr = aspectBackForExecute(cmdReturnStr,requestData, requestData.getRequest()); //aspect: 后置切面

        } catch (DataAccessException e) {
            e.printStackTrace();
            throw e;
        }

        return cmdReturnStr;
    }




    /**
     * 对request的数据进行处理,封装
     * @return
     */
    private void publicInitData(RequestData requestData, JSONObject commandConfig){

        //将request中的信息，枚举，并放在data中
        requestData.setData(InitDataUtil.getRequestData(requestData.getRequest()));

        //将session中的信息，枚举，并放在session中
        requestData.setSession(InitDataUtil.getSessionData(requestData.getRequest()));

        //将cookie中的信息，枚举，并放在cookie中
        requestData.setCookie(InitDataUtil.getCookieData(requestData.getRequest()));

        //从config.json中读取规则,做 data 段，数据的处理
        JSONObject dataJsonObject = commandConfig.getJSONObject("data");
        Iterator dataIt = dataJsonObject.keySet().iterator();       //枚举出所有不能为空的配置
        while (dataIt.hasNext()){   //"unit.id":"$seq('jiaorder','unit','$session.service_id')",
            String dataFiled = dataIt.next().toString();    //需要设置数据项的字段    unit.id
            HashMap data = getDataMapByFiled(requestData, dataFiled);
            String dataValueStr = (String)data.get(dataFiled);  //  $seq('jiaorder','unit','$session.service_id')
            String dataValue = getValueByRuleStr(requestData,dataValueStr); //  根据上面的dataValueStr运算后，版值 1000
            //设置默认值回到数据对象中
            putDataMapByFiled(requestData,dataFiled,dataValue);
        }

    }







    /**
     * 前切面:initdata
     * @param data
     */
    private void aspectFrontForInitdata(RequestData data){

        //TODO 根据config.json中配置的切面,动态加载切面类,并执行

    }

    /**
     * 后切面:initdata
     * @param data
     */
    private void aspectBackForInitdata(RequestData data){

    }


    /**
     * 根据config.json，以及data信息，做validate 做一次初始的validate
     * @param requestData
     * @param validate
     * @param commandConfig
     */
    public void publicValidate(RequestData requestData, ValidateUtil validate, JSONObject commandConfig){

        //从config.json中读取规则
        JSONObject validateJsonObject = commandConfig.getJSONObject("validate");

        //设置默认值
        JSONObject defJsonObject = validateJsonObject.getJSONObject("def");
        Iterator defIt = defJsonObject.keySet().iterator();       //枚举出所有不能为空的配置
        while (defIt.hasNext()){
            String defFiled = defIt.next().toString();    //不能为空的字段
            HashMap data = getDataMapByFiled(requestData,defFiled);
            if(!data.containsKey(defFiled) || StrUtil.isNull((String)data.get(defFiled))){    //如果为空了
                String value = (String) data.get(defFiled);     //得到值
                value = getValueByRuleStr(requestData,value);   //处理值中的变量
                //设置默认值回到数据对象中
                putDataMapByFiled(requestData,defFiled,value);
            }
        }

        //不能为空的
        JSONObject isNullJsonObject = validateJsonObject.getJSONObject("isNull");
        Iterator isNullIt = isNullJsonObject.keySet().iterator();       //枚举出所有不能为空的配置
        while (isNullIt.hasNext()){
            String isNullFiled = isNullIt.next().toString();    //不能为空的字段
            HashMap data = getDataMapByFiled(requestData,isNullFiled);
            if(!data.containsKey(isNullFiled) || StrUtil.isNull((String)data.get(isNullFiled))){    //如果为空了
                validate.addError(isNullJsonObject.getString(isNullFiled)); //添加错误消息
            }
        }

        //必须为数字的
        JSONObject isNumJsonObject = validateJsonObject.getJSONObject("isNum");
        Iterator isNumIt = isNumJsonObject.keySet().iterator();       //枚举出所有必须为数字的配置
        while (isNumIt.hasNext()){
            String isNumFiled = isNumIt.next().toString();    //不能为空的字段
            HashMap data = getDataMapByFiled(requestData, isNumFiled);
            if(!data.containsKey(isNumFiled) || StrUtil.isNull((String)data.get(isNumFiled))){    //如果为空了
                validate.addError(isNumJsonObject.getString(isNumFiled)); //添加错误消息
            }
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
     * 前切面:initdata
     * @param data
     */
    private void aspectFrontForValidate(RequestData data, ValidateUtil validate){

        //TODO 根据config.json中配置的切面,动态加载切面类,并执行

    }

    /**
     * 后切面:initdata
     * @param data
     */
    private void aspectBackForValidate(RequestData data, ValidateUtil validate){

    }




    public String publicExecute(RequestData requestData, HttpServletRequest request, JSONObject commandConfig) throws DataAccessException{
        String returnStr = "";

        //开始 execute 段处理
        JSONObject executeListJsonObject = commandConfig.getJSONObject("execute");  //  "execute":{
        Iterator executeListIt = executeListJsonObject.keySet().iterator();

        //支持多个SQL语句段处理
        while (executeListIt.hasNext()){
            JSONObject executeJsonObject = executeListJsonObject.getJSONObject(executeListIt.next().toString());    //"add":{

            //数据源
            String db = executeJsonObject.getString("DB");

            //缓存
            String cache = executeJsonObject.getString("CACHE");
            String cacheUpdate = executeJsonObject.getString("CACHE_UPDATE");
            String cacheReload = executeJsonObject.getString("CACHE_RELOAD");

            //SQL语句
            String sql = executeJsonObject.getString("SQL");    //TODO 下一步，可以支持json array的方式，在这里写多个SQL，就能支持批处理、事务了

            //IS_BATCH  批量执行
            String isBatch = executeJsonObject.getString("IS_BATCH");

            //IS_ROLLBACK 是否回滚
            String isRollback = executeJsonObject.getString("IS_ROLLBACK");

            if(StrUtil.isNotNull(sql)){
                CommonDAO dao = new CommonDAO();
                sql = sql.toLowerCase().trim();    //统一转换成大写,并去掉前后空格

                sql = this.getAllFiledValue(requestData,sql);

                if(sql.startsWith("SELECT")){
                    //TODO 这里应该处理结果集、处理分页
                    List<JSONObject> list = dao.select(sql);

                    //TODO 怎么把 list，结合 config.json 中的 return 参数，做对应的返回

                }else if(sql.startsWith("INSERT")){
                    dao.insert(sql);
                }else if(sql.startsWith("DELETE")){
                    dao.delete(sql);
                }else if(sql.startsWith("UPDATE")){     //UPDATE unit SET type='xx',state=1 WHERE service_id={$data.service_id}
                    dao.update(sql);
                }
            }
        }

        return returnStr;
    }


    /**
     * 前切面:initdata
     * @param data
     */
    private String aspectFrontForExecute(RequestData data, HttpServletRequest request)  throws DataAccessException{

        //TODO 根据config.json中配置的切面,动态加载切面类,并执行
        return "";
    }

    /**
     * 后切面:initdata
     * @param data
     */
    private String aspectBackForExecute(String returnStr,RequestData data, HttpServletRequest request)  throws DataAccessException{
        String overReturn = returnStr;

        return overReturn;
    }




    public abstract String initdata(RequestData $DATA) ;
    public abstract void validate(RequestData $DATA, ValidateUtil validate);
    public abstract String execute(RequestData $DATA, HttpServletRequest request) throws DataAccessException;

}
