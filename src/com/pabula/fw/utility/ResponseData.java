package com.pabula.fw.utility;

import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 将Response中需要放置的信息,放在这里
 * Created by Pabula on 16/6/27 22:27.
 */
public class ResponseData {


    private HttpServletResponse response = null;
    RequestData requestData = null;

    //要向前端返回的数据项集合（K V））
    HashMap param = new HashMap();

    //要向前端返回的结果集合（JSON LIST，即数据库查询的结果集）
    List<JSONObject> rows = new ArrayList<>();

    //返回的页面地址
    String url = "";

    //请求前操作的状态
    String state = "";

    JSONObject commandJsonObject;

    public ResponseData(HttpServletResponse response,RequestData requestData,JSONObject commandJsonObject) {
        this.response = response;
        this.commandJsonObject = commandJsonObject;
        this.requestData = requestData;
    }

    /**
     * 获得response中要返回的值
     * @return
     */
    public String getReturnJSON() {
        //JSON 格式的，带 CODE\MSG\DATA
        JSONObject returnJSONObject = new JSONObject(this.getParam());  //TODO 不确定这样，能不能直接传map

        JSONObject paramJsonObject = commandJsonObject.getJSONObject("return").getJSONObject("param");
        Iterator dataIt = paramJsonObject.keySet().iterator();       //枚举出所有不能为空的配置
        while (dataIt.hasNext()){   //"unit.id":"$seq('jiaorder','unit','$session.service_id')",
            String dataFiled = dataIt.next().toString();    //需要设置数据项的字段    unit.id
            String dataValue = requestData.parseVar(paramJsonObject.getString(dataFiled));

            //添加param
            returnJSONObject.put(dataFiled,dataValue);
        }


        returnJSONObject.put("list",this.getRows());    //列表   //TODO 可能会与map中的重名，需要有一个自己的名称空间，打算用$前缀，担心和前台js框架冲突
        returnJSONObject.put("state",this.getState());  //状态    TODO 同上

        //todo 如果有多个列表要向前台传送，怎么办？

        return returnJSONObject.toJSONString();
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public HashMap getParam() {
        return param;
    }

    public void setParam(HashMap param) {
        this.param = param;
    }

    public List<JSONObject> getRows() {
        return rows;
    }

    public void setRows(List<JSONObject> rows) {
        this.rows = rows;
    }
}
