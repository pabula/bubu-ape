package com.pabula.common.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * httpclient ������
 * Created by pabula on 15/8/28.
 */
public class HttpClientUtil {

    private static final CloseableHttpClient httpClient;
    public static final String CHARSET = "UTF-8";

    static {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    public static String doGet(String url, Map<String, String> params){
        return doGet(url, params,CHARSET);
    }
    public static String doPost(String url, Map<String, String> params){
        return doPost(url, params,CHARSET);
    }

    /**
     * HTTP Get ��ȡ����
     * @param url  �����url��ַ ?֮ǰ�ĵ�ַ
     * @param params ����Ĳ���
     * @param charset    �����ʽ
     * @return    ҳ������
     */
    public static String doGet(String url,Map<String,String> params,String charset){
        if(StrUtil.isNull(url)){
            return null;
        }
        try {
            if(params != null && !params.isEmpty()){
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for(Map.Entry<String,String> entry : params.entrySet()){
                    String value = entry.getValue();
                    if(value != null){
                        pairs.add(new BasicNameValuePair(entry.getKey(),value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null){
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * HTTP Post ��ȡ����
     * @param url  �����url��ַ ?֮ǰ�ĵ�ַ
     * @param params ����Ĳ���
     * @param charset    �����ʽ
     * @return    ҳ������
     */
    public static String doPost(String url,Map<String,String> params,String charset){
        if(StrUtil.isNull(url)){
            return null;
        }
        try {
            List<NameValuePair> pairs = null;
            if(params != null && !params.isEmpty()){
                pairs = new ArrayList<NameValuePair>(params.size());
                for(Map.Entry<String,String> entry : params.entrySet()){
                    String value = entry.getValue();
                    if(value != null){
                        pairs.add(new BasicNameValuePair(entry.getKey(),value));
                    }
                }
            }
            HttpPost httpPost = new HttpPost(url);
            if(pairs != null && pairs.size() > 0){
                httpPost.setEntity(new UrlEncodedFormEntity(pairs,CHARSET));
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null){
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
