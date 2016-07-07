package com.pabula.common.util;

import javax.servlet.http.HttpServletRequest;


/**
 * IPλ������
 * @author dekn
 * www.cms4j.com רҵ�� java /jsp ����վ���ݹ���ϵͳ
 * 2009-9-5 ����04:16:35
 */
public class IPUtil{

    /**
     * ��ÿͻ�IP
     * @param request
     * @return
     */
    public static String getRemortIP(HttpServletRequest request) {
        if (request.getHeader("x-forwarded-for") == null) {
            return request.getRemoteAddr();
        }
        return request.getHeader("x-forwarded-for");
    }

}
