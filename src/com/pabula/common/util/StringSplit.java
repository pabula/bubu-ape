/*
 * �������� 2006-4-25
 *
 */
package com.pabula.common.util;


import java.util.ArrayList;


/**

 * 
 */
public class StringSplit{
private  static StringSplit split=null;

public static synchronized StringSplit	getInstance(){
	if(split==null){
		split=new StringSplit();	
	}
	return split;
}
private StringSplit(){}
/**
 * �����ַ���ת���������c���ŷֿ�������list
 * ����list�еĶ�����String 
 * ע��ֻȡ�м��c���ŷֿ����ַ���һ�������һ��ûȡ
 * @param c�ָ����
 * @param str
 * @return
 */
public  static ArrayList split(char c,String str) {
	if(str==null){
		return null;
	}
	ArrayList tmp=new ArrayList();
	String temp=null;
	int k=0;
	int kk=0;
	int tt=0;
	for(int i=0;i<str.length();i++){
    if(str.charAt(i)==c){
    kk=i;
    temp=str.substring(tt,kk);
    tmp.add(temp);
    tt=i+1;	
	}
	}
	if(tmp.size()<=1){
		return null;
	}
	ArrayList list=new ArrayList();
	for(int j=1;j<tmp.size();j++){
		list.add(tmp.get(j));
	}
	return list;
}

/**
 * �����ַ�����c���Ž�ȡȥ��ǰ��������c��
 * �ŷֿ����ַ�����ֻȡ�м���ַ���
 * @param c�ָ����
 * @param str
 * @return String
 */
public static String gerCenterString(char c,String str){
	if(str==null){
			return null;
		}
    if(str.trim().equals("")){
    	return null;
	}
	int kk=0;
	int tt=0;
	int num=0;
	for(int i=0;i<str.length();i++ ){
	 if(str.charAt(i)==c){
	 	num++;	
	 }
	}
	for(kk=0;kk<str.length()&&str.charAt(kk)!=c;kk++){
	}
	for(tt=str.length()-1;tt>=0&&str.charAt(tt)!=c;tt--){
	}
	if(num<=1){
		return null;
	}
	String centerString=str.substring(kk+1,tt);
	return centerString;
}

public static void main(String args[]){//���ڲ���
	String test="start,123#sdw,456#jikui,end";
	//ArrayList list=split(',','#',test);
}}
