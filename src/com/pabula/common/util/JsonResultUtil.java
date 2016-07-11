package com.pabula.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.jiaorder.ENV;
import com.pabula.common.logger.Logger;

/**
 * Created by sunsai on 2015/10/23.
 */
public class JsonResultUtil {

    public static final int OK = 0;         //OK
    public static final int ERROR = 400;    //ERROR 用于不需要细分具体错误原因的场景，就是错了
    public static final int EXCEPTION = 500;//异常 用于不需要细分具体错误原因的场景，就是抛异常了
    public static final int FORM_ERROR = 4000;
    public static final int ORDER_STATE_NOT_UNITE = 401;

    public static final int ORDER_PAY_ERROR = 402; //订单支付错误


    public static final int JSON_PARSE_EXCEPTION = 400001;
    public static final int JSON_MAPPER_EXCEPTION = 400002;

    public static JsonResultUtil instance() {
        return new JsonResultUtil();
    }

    public static JsonResultUtil instance(int code, String msg, Object data) {
        return new JsonResultUtil(code, msg, data);
    }

    public static JsonResultUtil instance(int code, String phone_id, String msg, Object data) {
        return new JsonResultUtil(code, msg, data);
    }

    public JsonResultUtil addData(Object data) {
        this.data = data;
        return this;
    }

    public JsonResultUtil addCode(int code) {
        this.code = code;
        return this;
    }

    public JsonResultUtil addMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public JsonResultUtil addExtraData(Object extraData) {
        this.extraData = extraData;
        return this;
    }

    /**
     * @param data
     * @return
     */
    public static JsonResultUtil instance(Object data) {
        return new JsonResultUtil(0, "ok", data);
    }

    /**
     * @return
     */
    public static String ok() {
        return new JsonResultUtil(OK, "ok", "ok").json();
    }

    /**
     * @return
     */
    public static String error() {
        return new JsonResultUtil(ERROR, "error", "error").json();
    }

    public static String error(String errMessage) {
        return new JsonResultUtil(ERROR, errMessage, "error").json();
    }

    private JsonResultUtil(int code, String msg, Object data) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    private JsonResultUtil() {
        this.data = "";
        this.code = OK;
        this.msg = "ok";
    }

    public static JsonResultUtil parse(String json) {
        //JSONObject jsonObject = JSONObject.fromObject(json);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, JsonResultUtil.class);
        } catch (Exception e) {
            return new JsonResultUtil(JSON_PARSE_EXCEPTION, "parseException", "");
        }
    }

    private int code;

    private String msg;

    private Object data;

    private Object extraData;


    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    public Object getExtraData() {
        return extraData;
    }


    public String json() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter(ENV.JSON_FILTER_NAME,
                SimpleBeanPropertyFilter.serializeAllExcept());
        String json = null;

        try {
            mapper.setFilterProvider(filterProvider);
            json = mapper.writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
            json = "{'code':40002, 'msg':'mapperException','data':''}";
        }

        //JSONObject jsonObject = new JSONObject();
        //
        //jsonObject.put("code", code);
        //jsonObject.put("msg", msg);
        //jsonObject.put("data", data);
        //String result = jsonObject.toString();
        Logger.tag(JsonResultUtil.class.getSimpleName()).d(json);
        return json;
    }

    public String json(String filter, String filterField) {

        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter(filter,
                SimpleBeanPropertyFilter.serializeAllExcept(filterField));

        ObjectMapper mapper = new ObjectMapper();

        String json = null;

        try {
            json = mapper.writer(filterProvider).writeValueAsString(this);
            ;
        } catch (Exception e) {
            e.printStackTrace();
            json = "{'code':40002, 'msg':'mapperException','data':''}";
        }

        Logger.tag(JsonResultUtil.class.getSimpleName()).d(json);
        return json;
    }

    public String json(String filterField) {
        String[] filterFields = null;
        if (filterField.contains(",")) {
            filterFields = filterField.split(",");
        } else {
            filterFields = new String[]{filterField};
        }

        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter(ENV.JSON_FILTER_NAME,
                SimpleBeanPropertyFilter.serializeAllExcept(filterFields));

        ObjectMapper mapper = new ObjectMapper();

        String json = null;

        try {
            json = mapper.writer(filterProvider).writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
            json = "{'code':40002, 'msg':'mapperException','data':'出错了'}";
        }

        Logger.tag(JsonResultUtil.class.getSimpleName()).d(json);
        return json;
    }


    public static void main(String[] args) {

    }
}
