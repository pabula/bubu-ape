package com.pabula.common.util;

import java.util.Random;

/**
 *
 * ����������㷨������΢�ź��Լ���������token����
 * Created by sunsai on 2015/9/22.
 */
public class RandomUtil {


    public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LETTERCHAR = "abcdefghijkllmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERCHAR = "0123456789";

    private static final int MIN_LENTH = 10;

    private static final int MAX_LENTH = 32;

    public static String randomStr(int length) {
        if(length < MIN_LENTH) {
            length = MIN_LENTH;
        }
        if(length > MAX_LENTH) {
            length = MAX_LENTH;
        }
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        int strLength = ALLCHAR.length();

        for(int i = 0; i< length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(strLength)));
        }

        return sb.toString();

    }


    public static void main(String[] args) {

        for(int i = 0; i< 50; i++) {
            String result = RandomUtil.randomStr(i);
            System.out.println(result);
        }
    }

}
