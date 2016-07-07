/*
 * Created on 2005-6-15
 */
package com.pabula.common.util;


import java.security.MessageDigest;

/**
 * @author Dekn
 * MD5����
 */
public class MD5 {
	private final static String[] hexDigits = { 
	      "0", "1", "2", "3", "4", "5", "6", "7", 
	      "8", "9", "a", "b", "c", "d", "e", "f"}; 

	  /** 
	   * ת���ֽ�����Ϊ16�����ִ� 
	   * @param b �ֽ����� 
	   * @return 16�����ִ� 
	   */ 

	  public static String byteArrayToHexString(byte[] b) { 
	    StringBuffer resultSb = new StringBuffer(); 
	    for (int i = 0; i < b.length; i++) { 
	      resultSb.append(byteToHexString(b[i])); 
	    } 
	    return resultSb.toString(); 
	  } 

	  private static String byteToHexString(byte b) { 
	    int n = b; 
	    if (n < 0) 
	      n = 256 + n; 
	    int d1 = n / 16; 
	    int d2 = n % 16; 
	    return hexDigits[d1] + hexDigits[d2]; 
	  } 

	  public static String MD5Encode(String origin) { 
	    String resultString = null; 

	    try { 
	      resultString=new String(origin); 
	      MessageDigest md = MessageDigest.getInstance("MD5"); 
	      resultString=byteArrayToHexString(md.digest(resultString.getBytes())); 
	      /**********************************************
	       * ����MD5���룬��ԭ���ַ�ƽ���ķݽ�������			*
	       * ����������2314 ���ڶ����ڵ�һλ���������ڵڶ�λ *
	       **********************************************/
	      StringBuffer sb = new StringBuffer();
	      sb.append(resultString.substring(8,16)); //�ڶ���
	      sb.append(resultString.substring(16,24)); //������
	      sb.append(resultString.substring(0,8)); //��һ��
	      sb.append(resultString.substring(24)); //���ķ�
	      
	      resultString  = sb.toString();
	    } 
	    catch (Exception ex) { 

	    } 
	    return resultString; 
	  }




}
