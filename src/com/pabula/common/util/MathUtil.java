package com.pabula.common.util;

import java.math.BigDecimal;

/**
 * ��ѧ���㹤����
 * Created by Pabula on 2015/7/9.
 */
public class MathUtil {

    //Ĭ�ϳ������㾫��

    private static final int DEFAULT_DIV_SCALE = 10;

    private static final int DEFAULT_XIAOSHUDIAN = 2;//С����λ��


    /**
     * �ṩ��ȷ�ļӷ����㡣
     * @param v1
     * @param v2
     * @return ���������ĺ�
     */

    public static double add(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return round(b1.add(b2).doubleValue(), DEFAULT_XIAOSHUDIAN);
    }

    /**
     * �ṩ��ȷ�ļӷ�����
     * @param v1
     * @param v2
     * @return ����������ѧ�Ӻͣ����ַ�����ʽ����
     */
    public static String add(String v1, String v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return round(b1.add(b2).toString(), DEFAULT_XIAOSHUDIAN);
    }


    /**
     * �ṩ��ȷ�ļ������㡣
     * @param v1
     * @param v2
     * @return ���������Ĳ�
     */
    public static double subtract(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return round(b1.subtract(b2).doubleValue(), DEFAULT_XIAOSHUDIAN);
    }


    /**
     * �ṩ��ȷ�ļ�������
     * @param v1
     * @param v2
     * @return ����������ѧ����ַ�����ʽ����
     */
    public static String subtract(String v1, String v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return round(b1.subtract(b2).toString(), DEFAULT_XIAOSHUDIAN);
    }


    /**
     * �ṩ��ȷ�ĳ˷����㡣
     * @param v1
     * @param v2
     * @return ���������Ļ�
     */
    public static double multiply(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return round(b1.multiply(b2).doubleValue(), DEFAULT_XIAOSHUDIAN);
    }


    /**
     * �ṩ��ȷ�ĳ˷�����
     * @param v1
     * @param v2
     * @return ������������ѧ�������ַ�����ʽ����
     */
    public static String multiply(String v1, String v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return round(b1.multiply(b2).toString(), DEFAULT_XIAOSHUDIAN);
    }


    /**
     * �ṩ����ԣ���ȷ�ĳ������㣬�����������������ʱ����ȷ��
     * <p/>
     * С�����Ժ�10λ���Ժ��������������,����ģʽ����ROUND_HALF_EVEN
     * @param v1
     * @param v2
     * @return ������������
     */
    public static double divide(double v1, double v2){
        return round(divide(v1, v2, DEFAULT_DIV_SCALE), DEFAULT_XIAOSHUDIAN);
    }


    /**
     * �ṩ����ԣ���ȷ�ĳ������㡣�����������������ʱ����scale����ָ
     * <p/>
     * �����ȣ��Ժ�������������롣����ģʽ����ROUND_HALF_EVEN
     * @param v1
     * @param v2
     * @param scale ��ʾ��Ҫ��ȷ��С�����Ժ�λ��
     * @return ������������
     */
    public static double divide(double v1, double v2, int scale){
        return divide(v1, v2, scale, BigDecimal.ROUND_HALF_EVEN);
    }


    /**
     * �ṩ����ԣ���ȷ�ĳ������㡣�����������������ʱ����scale����ָ
     * <p/>
     * �����ȣ��Ժ�������������롣����ģʽ�����û�ָ������ģʽ
     * @param v1
     * @param v2
     * @param scale      ��ʾ��Ҫ��ȷ��С�����Ժ�λ
     * @param round_mode ��ʾ�û�ָ��������ģʽ
     * @return ������������
     */
    public static double divide(double v1, double v2, int scale, int round_mode) {
        if (scale < 0){
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, round_mode).doubleValue();
    }


    /**
     * �ṩ����ԣ���ȷ�ĳ������㣬�����������������ʱ����ȷ��
     * <p/>
     * С�����Ժ�10λ���Ժ��������������,����ģʽ����ROUND_HALF_EVEN
     * @param v1
     * @param v2
     * @return �����������̣����ַ�����ʽ����
     */
    public static String divide(String v1, String v2){
        return round(divide(v1, v2, DEFAULT_DIV_SCALE), DEFAULT_XIAOSHUDIAN);
    }


    /**
     * �ṩ����ԣ���ȷ�ĳ������㡣�����������������ʱ����scale����ָ
     * <p/>
     * �����ȣ��Ժ�������������롣����ģʽ����ROUND_HALF_EVEN
     * @param v1
     * @param v2
     * @param scale ��ʾ��Ҫ��ȷ��С�����Ժ�λ
     * @return �����������̣����ַ�����ʽ����
     */
    public static String divide(String v1, String v2, int scale){
        return divide(v1, v2, DEFAULT_DIV_SCALE, BigDecimal.ROUND_HALF_EVEN);
    }


    /**
     * �ṩ����ԣ���ȷ�ĳ������㡣�����������������ʱ����scale����ָ
     * <p/>
     * �����ȣ��Ժ�������������롣����ģʽ�����û�ָ������ģʽ
     * @param v1
     * @param v2
     * @param scale      ��ʾ��Ҫ��ȷ��С�����Ժ�λ
     * @param round_mode ��ʾ�û�ָ��������ģʽ
     * @return �����������̣����ַ�����ʽ����
     */
    public static String divide(String v1, String v2, int scale, int round_mode){
        if (scale < 0){
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }

        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, scale, round_mode).toString();
    }


    /**
     * �ṩ��ȷ��С��λ�������봦��,����ģʽ����ROUND_HALF_EVEN
     *
     * @param v     ��Ҫ�������������
     * @param scale С���������λ
     * @return ���������Ľ��
     */

    public static double round(double v, int scale){
        return round(v, scale, BigDecimal.ROUND_HALF_EVEN);
    }

    /**
     * �ṩ��ȷ��С��λ�������봦��
     * @param v          ��Ҫ�������������
     * @param scale      С���������λ
     * @param round_mode ָ��������ģʽ
     * @return ���������Ľ��
     */
    public static double round(double v, int scale, int round_mode){
        if (scale < 0){
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }

        BigDecimal b = new BigDecimal(Double.toString(v));
        return b.setScale(scale, round_mode).doubleValue();
    }


    /**
     * �ṩ��ȷ��С��λ�������봦��,����ģʽ����ROUND_HALF_EVEN
     * @param v     ��Ҫ�������������
     * @param scale С���������λ
     * @return ���������Ľ�������ַ�����ʽ����
     */
    public static String round(String v, int scale){
        return round(v, scale, BigDecimal.ROUND_HALF_EVEN);
    }

    /**
     * �ṩ��ȷ��С��λ�������봦��
     * @param v          ��Ҫ�������������
     * @param scale      С���������λ
     * @param round_mode ָ��������ģʽ
     * @return ���������Ľ�������ַ�����ʽ����
     */
    public static String round(String v, int scale, int round_mode){
        if (scale < 0){
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(v);
        return b.setScale(scale, round_mode).toString();
    }

}
