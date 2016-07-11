package com.pabula.common.util;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.jiaorder.ENV;

import java.util.HashMap;
import java.util.Set;

/**
 * 短信验证码工具类
 * Created by pabula on 15/7/30 01:32.
 */
public class SMSCheckCodeUtil {

    /**
     * 发送短信验证码
     * @param phone 接收人电话，多人用半角逗号分隔
     * @param checkCode 验证码
     * @param recleTime 验证码周期（分钟）
     */
    public static void sendCheckCode(String phone,String checkCode,String recleTime){
        HashMap<String, Object> result = null;

        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init(ENV.SMS_SERVER, ENV.SMS_SERVER_PORT);// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount(ENV.SMS_ACCOUNT_SID, ENV.SMS_AUTH_TOKEN);// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId(ENV.SMS_APP_ID);// 初始化应用ID

        result = restAPI.sendTemplateSMS(phone, ENV.SMS_TELPLET_ID ,new String[]{checkCode,recleTime});
        System.out.println("短信发送结果：" + result);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println("短信发送结果: " + key +" = "+object);
            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("短信发送错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }

    /**
     * 发送短信验证码
     * @param phone 接收人电话，多人用半角逗号分隔
     * @param newpwd 新密码
     * @param recleTime 验证码周期（分钟）
     */
    public static void sendNewPwd(String phone,String newpwd,String recleTime){
        HashMap<String, Object> result = null;

        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init(ENV.SMS_SERVER, ENV.SMS_SERVER_PORT);// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount(ENV.SMS_ACCOUNT_SID, ENV.SMS_AUTH_TOKEN);// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId(ENV.SMS_APP_ID);// 初始化应用ID

        result = restAPI.sendTemplateSMS(phone, ENV.SMS_TELPLET_ID_NEWPWD ,new String[]{newpwd,recleTime});
        System.out.println("短信发送结果：" + result);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println("短信发送结果: " + key +" = "+object);
            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("短信发送错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }


    /**
     * 发送短信验证码
     * @param phone 接收人电话，多人用半角逗号分隔
     * @param newpwd 新密码
     * @param recleTime 验证码周期（分钟）
     */
    public static void sendResetPwdRandCode(String phone,String newpwd,String recleTime){
        HashMap<String, Object> result = null;

        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init(ENV.SMS_SERVER, ENV.SMS_SERVER_PORT);// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount(ENV.SMS_ACCOUNT_SID, ENV.SMS_AUTH_TOKEN);// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId(ENV.SMS_APP_ID);// 初始化应用ID

        result = restAPI.sendTemplateSMS(phone, ENV.SMS_TELPLET_ID_RESETPWD ,new String[]{newpwd,recleTime});
        System.out.println("短信发送结果：" + result);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println("短信发送结果: " + key +" = "+object);
            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("短信发送错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        HashMap<String, Object> result = null;

        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init(ENV.SMS_SERVER, ENV.SMS_SERVER_PORT);// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount(ENV.SMS_ACCOUNT_SID, ENV.SMS_AUTH_TOKEN);// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId(ENV.SMS_APP_ID);// 初始化应用ID

        result = restAPI.sendTemplateSMS("13966667756",ENV.SMS_TELPLET_ID ,new String[]{"1998","23"});

        System.out.println("SDKTestSendTemplateSMS result=" + result);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }

}

