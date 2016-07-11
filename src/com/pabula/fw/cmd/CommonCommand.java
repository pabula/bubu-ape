package com.pabula.fw.cmd;

import com.alibaba.fastjson.JSON;
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
import com.pabula.fw.utility.ResponseData;
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
    public void main(RequestData requestData,ResponseData responseData ,Command command, JSONObject commandConfig,ValidateUtil validate) throws DataAccessException{

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
        try {
            publicExecute(requestData, requestData.getRequest(), commandConfig, responseData);    //不需要利用返回值

            aspectFrontForExecute(requestData, requestData.getRequest(),responseData); //aspect 前置切面，因为这里不是最后一个执行方法，所以不需要返回值赋值
            command.execute(requestData, requestData.getRequest(),responseData);    //执行 execute
            aspectBackForExecute(requestData, requestData.getRequest(),responseData); //aspect 后置切面

        } catch (DataAccessException e) {
            e.printStackTrace();
            throw e;
        }

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


    /**
     * 框架层执行，会根据config.json中的配置，做相应的业务逻辑
     * @param requestData
     * @param request
     * @param commandConfig
     * @return
     * @throws DataAccessException
     */
    public void publicExecute(RequestData requestData, HttpServletRequest request, JSONObject commandConfig,ResponseData responseData) throws DataAccessException{

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
                    responseData.setRows(dao.select(sql));  //查询SQL，并将结果集，返回成 list<JSONObject>格式，并放到 responseData 中
                }else if(sql.startsWith("INSERT")){
                    //TODO 可能还不支持 INSERT INTO unit ({$ALL_COLUMN}) values ({$DATA}) 这样的all_column写法
                    responseData.setState(String.valueOf(dao.insert(sql)));
                }else if(sql.startsWith("DELETE")){
                    responseData.setState(String.valueOf(dao.delete(sql)));
                }else if(sql.startsWith("UPDATE")){     //UPDATE unit SET type='xx',state=1 WHERE service_id={$data.service_id}
                    responseData.setState(String.valueOf(dao.update(sql)));
                }
            }
        }

    }


    /**
     * 前切面:initdata
     * @param data
     */
    private void aspectFrontForExecute(RequestData data, HttpServletRequest request,ResponseData responseData)  throws DataAccessException{
        //TODO 根据config.json中配置的切面,动态加载切面类,并执行
    }

    /**
     * 后切面:initdata
     * @param data
     */
    private void aspectBackForExecute(RequestData data, HttpServletRequest request,ResponseData responseData)  throws DataAccessException{
    }




    public abstract String initdata(RequestData $DATA) ;
    public abstract void validate(RequestData $DATA, ValidateUtil validate);
    public abstract String execute(RequestData $DATA, HttpServletRequest request) throws DataAccessException;

}
