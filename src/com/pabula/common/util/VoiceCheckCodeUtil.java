package com.pabula.common.util;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.jiaorder.ENV;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by sunsai on 2015/9/15.
 */
public class VoiceCheckCodeUtil {

    public static void sendVoiceCheckCode(String phone, String checkCode, String times){
        HashMap<String, Object> result = null;

        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init(ENV.SMS_SERVER, ENV.SMS_SERVER_PORT);// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount(ENV.SMS_ACCOUNT_SID, ENV.SMS_AUTH_TOKEN);// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId(ENV.SMS_APP_ID);// 初始化应用ID
        //("验证码内容", "号码","显示的号码","3(播放次数)","状态通知回调地址", "语言类型", "第三方私有数据");
        result = restAPI.voiceVerify(checkCode,phone,"18661047778",times,"","zh","");
        System.out.println(result.get("statusCode"));
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap data = (HashMap) result.get("data");
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

    public static void main(String[] args) {
        // System.out.println("test");
        // sendVoiceCheckCode("18661047778","1234","3");
    }
}