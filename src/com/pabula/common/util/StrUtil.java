/*
 * 创建于 2004-10-25 20:06:55
 * JCMS
 */
package com.pabula.common.util;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dekn
 *
 * 字符串工具类
 *
 * JCMS ( Content Manager System for java )
 */
public class StrUtil {

	private static String varStr = "@";

	/**
	 * 替换模板中的字符
	 * @param str	原字符串
	 * @param templetStr 要替换的模板字符
	 * @param data 替换成什么字符
	 * @return
	 */
	public static String replaceAllForTemplet(String str,String templetStr,
			String data){
		String s = replaceAll(str,varStr + templetStr,data);
		return s;
	}

	/**
	 * 取得一个非空的整数值，如果提供的数据为空或者为字母，则自动转换为0
	 * @param data　要转换的数据
	 * @return
	 */
	public static int getNotNullIntValue(String data){
		int value = 0;
		try {
			value = Integer.parseInt(data);
			//            if (value < 0){
			//                value = 0;
			//            }
		}catch (NumberFormatException e){
			value = 0;
		}
		return value;
	}

	/**
	 * 取得一个非空的整数值，如果提供的数据为空或者为字母，则返回returnValue
	 * @param date
	 * @param returnValue
	 * @return
	 */
	public static int getNotNullIntValue(String date,int returnValue){
		int value = getNotNullIntValue(date);
		if (value == 0) {
			value = returnValue;
		}
		return value;
	}

	/**
	 * 取得一个非空的时间戳，如果提供的数据为空或为NULL，则自动取当前时间
	 * @param data
	 * @param dateFormat TODO
	 * @return
	 */
	public static Timestamp getNotNullTimestampValue(String data, String dateFormat){
		Timestamp value;
		try{
			if (null == data || data.equals("")){
				value = new Timestamp(System.currentTimeMillis());
			}else{
				SimpleDateFormat smd = new SimpleDateFormat( dateFormat);
				value = new Timestamp(smd.parse(data).getTime());
			}
		}catch(Exception e){
			e.printStackTrace();
			value = new Timestamp(System.currentTimeMillis());
		}

		return value;
	}

	/**
	 * 返回一个非空的字符串，如果字符串为空或为NULL，则返回returnValue
	 * @param date
	 * @param returnValue 如果字符串为空或为NULL，则返回returnValue
	 * @return
	 */
	public static String getNotNullStringValue(String date,String returnValue){
		String value = date;
		if (date == null || date.trim().equals("")){
			value = returnValue;
		}
		return value;
	}

	/**
	 * 返回一个非空的字符串，如果字符串为空，则直接返回“”（空字符，非null)
	 * @param date
	 * @return
	 */
	public static String getNotNullStringValue(String date){
		String value = date;
		if (date == null || date.trim().equals("")){
			return "";
		}
		return value;
	}

	/**
	 * 判断一个字符串是不是数字
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str){
		boolean isNumber = true;
		try {
			int tmp = Integer.parseInt(str.trim());
			if (tmp < 0){
				return false;
			}
		}catch (NumberFormatException e){
			return false;
		}
		return isNumber;
	}


	/**
	 * 判断一个字符串是不是FLOAT类型的数字
	 * @param str
	 * @return
	 */
	public static boolean isFloat(String str){
		boolean isFloat = true;
		try {
			float tmp = Float.parseFloat(str.trim());
			if (tmp < 0){
				return false;
			}
		}catch (NumberFormatException e){
			return false;
		}
		return isFloat;
	}


	/**
	 * 字符替换函数（JDK1.4那个性能有问题）
	 * @return String
	 */
	public static String replaceAll(String source, String oldString,String newString) {
		//被替换字符为空，返回源字符串
		if(null == oldString || oldString.equals("")){
			return source;
		}
		//源为空，返回null
		if(isNull(source)){
			return "";
		}

		StringBuffer output = new StringBuffer();
		int lengOfsource = source.length();
		int lengOfold = oldString.length();
		int posStart = 0;
		int pos;
		while ((pos = source.indexOf(oldString, posStart)) >= 0) {
			output.append(source.substring(posStart, pos));
			output.append(newString);
			posStart = pos + lengOfold;
		}
		if (posStart < lengOfsource) {
			output.append(source.substring(posStart));
		}
		return output.toString();
	}


	/**
	 * 替换所有空格
	 * @param source
	 * @return
	 */
	public static String replaceAllSpace(String source){
		return replaceAll(source," ","");
	}

	/**
	 * 检测字符串是否符合规则
	 * @param source
	 * @return
	 */
	public static boolean checkStringRule(String source){
		boolean isOK = true;

		if(null == source || source.trim().equals("")){
			return true;
		}

		if(source.indexOf("<") > -1 ||
				source.indexOf(">") > -1 ||
				source.indexOf(".") > -1 ||
				source.indexOf("\"") > -1 ||
				source.indexOf("\'") > -1 ||
				source.indexOf(",") > -1 ||
				source.indexOf("$") > -1 ||
				source.indexOf("#") > -1 ||
				source.indexOf("%") > -1 ||
				source.indexOf("!") > -1 ||
				source.indexOf("~") > -1 ||
				source.indexOf("^") > -1 ||
				source.indexOf("&") > -1 ||
				source.indexOf("*") > -1 ||
				source.indexOf("(") > -1 ||
				source.indexOf(")") > -1 ||
				source.indexOf("=") > -1 ||
				source.indexOf("+") > -1 ||
				source.indexOf("|") > -1 ||
				source.indexOf("\\") > -1 ||
				source.indexOf(";") > -1 ||
				source.indexOf(":") > -1 ||
				source.indexOf("?") > -1 ||
				source.indexOf("　") > -1
				)

			isOK = false;



		return isOK;
	}


	/**
	 * 将HTML进行编码，以便前台PAGE可以正常显示
	 * @param str 要编码的字符串
	 * @return
	 */
	public static String encodeHTML(String str){
		if(null == str){
			return str;
		}
		String s = str;
		s = replaceAll(s,"\"","&quot;");
		s = replaceAll(s,"<", "&lt;");
		s = replaceAll(s,">", "&gt;");
		s = replaceAll(s,"'", "&#39;");
		return s;
	}


	/**
	 * HTML解码
	 * @param str
	 * @return
	 * @author lala 2008-2-21 下午05:01:21
	 */
	public static String decodeHTML(String str){
		if(isNull(str))
			return "";

		String s = str;
		s = replaceAll(s,"&quot;","\"");
		s = replaceAll(s,"&lt;", "<");
		s = replaceAll(s,"&gt;", ">");
		return s;
	}

	/**
	 * 字符串转换成Vector,字符串必须以逗号分隔.
	 * @param str
	 * @return
	 */
	public static Vector strToVector(String str){
		Vector v = new Vector();

		if(null == str || str.trim().equals("")){
			return v;
		}

		String[] str_array = str.split("\\,");

		for (int i = 0; i < str_array.length; i++) {
			String tmp = str_array[i].trim().toLowerCase();
			if(null != tmp && !tmp.equals("")){
				v.add(tmp);
			}
		}

		return v;
	}

	/**
	 * 将一个字符型数组，转换为一个字符串
	 * @param strArray
	 * @param splitStr 转换成字符串时，各数组元素之间的分隔符
	 * @return
	 */
	public static String strArrayToStr(String[] strArray,String splitStr){
		String str = "";
		if(null == strArray || strArray.length <=0){
			return "";
		}

		for (int i = 0; i < strArray.length; i++) {
			if(str.length() > 0){
				str = str + "," + strArray[i];
			}else{
				str = strArray[i];
			}
		}

		return str;
	}


	/**
	 * 将字符串数据转换成Vector
	 * @param str_array
	 * @return
	 */
	public static Vector strArrayToVector(String[] str_array){
		Vector v = new Vector();

		if(null == str_array || str_array.length == 0){
			return v;
		}

		for (int i = 0; i < str_array.length; i++) {
			String tmp = str_array[i].trim();
			if(null != tmp && !tmp.equals("")){
				v.add(tmp);
			}
		}

		return v;
	}


	/**
	 * 合并两个vector，最终返回一个新的合并后的vector
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static Vector vectorUnite(Vector v1,Vector v2){
		Vector over = new Vector();

		over.addAll(v1);
		over.addAll(v2);

		return over;
	}


	/**
	 * 将hashmap转换成collection
	 * 此方法只能将hashmap中的值放入collection中
	 * @param map
	 * @return
	 */
	public static Collection mapToCollection(HashMap map){
		return map.values();
	}


	/**
	 * 判断提供的字符串是为空或NULL
	 * @return
	 */
	public static boolean isNull(String str){
		if(null == str || str.trim().equals("")|| str.trim().equals("null")){
			return true;
		}

		return false;
	}


	/**
	 * 判断提供的字符串是不是不为空或NULL
	 * @param str
	 * @return
	 */
	public static boolean isNotNull(String str){
		return !isNull(str);
	}


	/**
	 * 提供的字符是否比较运算符
	 * @param str
	 * @return
	 */
	public static boolean isCompareStr(String str){
		boolean b = false;

		if(isNull(str)){
			return false;
		}

		if(str.equals("<") || str.equals(">") || str.equals(">=") || str.equals("<=") || str.equals("=") || str.equals("==")){
			b = true;
		}

		return b;
	}



	/**
	 * 去除非法字符
	 * @param str
	 * @return
	 */
	public static String removeLawlessStr(String str){

		if(null == str || str.trim().equals("")){
			return str;
		}

		String overStr = str;

		overStr = StrUtil.replaceAll(overStr,"'","");
		overStr = StrUtil.replaceAll(overStr,"%","");
		overStr = StrUtil.replaceAll(overStr,"SELECT","[SELECT]");
		overStr = StrUtil.replaceAll(overStr,"UPDATE","[UPDATE]");
		overStr = StrUtil.replaceAll(overStr,"DELETE","[DELETE]");

		return overStr;
	}

	/**
	 * 除外HTML TAG
	 * @param str
	 * @return
	 */
	public static String removeHTMLTAG(String str){
		int beginHtml, endHtml;
		StringBuffer content = new StringBuffer(str);
		while (true) {
			beginHtml = content.indexOf("<");
			endHtml = content.indexOf(">");
			if (beginHtml >= 0 && endHtml >= 0 && beginHtml < endHtml) {
				content = content.replace(beginHtml, endHtml + 1, "");
			} else {
				break;
			}
		}

		return content.toString();
	}


	/**
	 * 编码HTML，并去除非法字符
	 * @param str
	 * @return
	 */
	public static String encodeHTMLAndRemoveLawlessStr(String str){
		String s = encodeHTML(str);
		return removeLawlessStr(s);
	}

	/**
	 * 将一个字符串，拆分成一个ArrayList
	 * @param c
	 * @param str
	 * @return
	 */
	public static ArrayList split(String str,char c) {
		if (str == null) {
			return null;
		}
		ArrayList tmp = new ArrayList();
		String temp = null;
		int kk = 0;
		int tt = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == c) {
				kk = i;
				temp = str.substring(tt, kk);
				tmp.add(temp);
				tt = i + 1;
			}
		}
		if (tt < str.length()) {
			tmp.add(str.substring(tt));
		}
		if (tmp.size() <= 0) {
			return null;
		}
		return tmp;
	}

	/**
	 * 将Vector转换成半角逗号分隔的字符串
	 * @param v
	 * @return
	 * @author dekn 2007-9-1 1:25:39
	 */
	public static String vectorToStr(Vector v){
		String s = "";

		Enumeration e = v.elements();
		while (e.hasMoreElements()) {
			String tmp = (String) e.nextElement();
			if(s.length() > 0){
				s = s + "," + tmp;
			}else{
				s = tmp;
			}
		}

		return s;
	}



	/**
	 * 取字符串的前toCount个字符
	 * @param str 被处理字符串
	 * @param toCount 截取长度
	 * @param more 后缀字符串
	 * @version 2004.11.24
	 * @author zhulx
	 * @return String
	 */
	public static String substring(String str, int toCount,String more){
		int reInt = 0;
		String reStr = "";
		if (null == str){
			return "";
		}

		char[] tempChar = str.toCharArray();
		for (int kk = 0; (kk < tempChar.length && toCount > reInt); kk++) {
			String s1 = str.valueOf(tempChar[kk]);
			byte[] b = s1.getBytes();
			reInt += b.length;
			reStr += tempChar[kk];
		}

		if (toCount == reInt || (toCount == reInt - 1)){
			reStr += more;
		}
		return reStr;
	}


	/**
	 * 功能：根据限制长度截取字符串（字符串中包括汉字，一个汉字等于两个字符）
	 * @param strParameter 要截取的字符串
	 * @param limitLength 截取的长度
	 * @return 截取后的字符串
	 */



	/**
	 * 取得一个字符串的长度（按字节）
	 * @param str
	 * @return
	 * @author dekn   2007-9-20 下午09:59:11
	 */
	public static int getLength(String str){
		int length = str.length();

		try {
			length = str.getBytes("GBK").length;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return length;
	}

	/**
	 * 判断某个字符串，是否在字符数组中，不区分大小写
	 * @param array
	 * @param str
	 * @return
	 * @author dekn   2007-10-26 上午12:07:25
	 */
	public static boolean isHasInArray(String[] array,String str){
		boolean is = false;

		for(int i =0;i<array.length;i++){
			if(array[i].equalsIgnoreCase(str)){
				return true;
			}
		}

		return is;
	}

	/**
	 * 取得hashMap中key的组合字符串，以,分隔
	 * @param map
	 * @return
	 * @author dekn   2008-6-6 上午10:36:25
	 */
	public static String getMapKeyStr(HashMap map){
		Iterator it = map.keySet().iterator();
		String over = "";

		while (it.hasNext()) {
			String key = (String) it.next();
			if(over.equals("")){
				over = key;
			}else{
				over = over + "," + key;
			}
		}

		return over;
	}

	/**
	 * 取得hashMap中value的组合字符串，以,分隔
	 * @param map
	 * @return
	 * @author dekn   2008-6-6 上午10:36:25
	 */
	public static String getMapValueStr(HashMap map){
		Iterator it = map.values().iterator();
		String over = "";

		while (it.hasNext()) {
			String value = (String) it.next();
			if(over.equals("")){
				over = value;
			}else{
				over = over + "," + value;
			}
		}

		return over;
	}


	/**
	 * 转义sql中的关键字
	 * @param str
	 * @return
	 * @author dekn   2008-11-13 下午07:03:51
	 */
	public static String encodeSqlStr(String str){
		if(isNull(str))
			return "";

		String s = str;

		s = replaceAll(s, "'", "\\'");	//将'转义成\'

		return s;
	}


	/**
	 * 判断字符串中是否含有中文
	 * @param source
	 * @return
	 * @author lala 2009-8-17 下午02:13:11
	 */
	public static boolean isChinese(String source){
		boolean retPar = false;

		String regex="[^u4E00-u9FA5]+";

		Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		Matcher matcher = pattern.matcher(source);

		if(matcher.find()){
			retPar = true;
		}

		return retPar;
	}

	/**
	 * 取得一个非空的浮点数，如果提供的数据为空或者为字母，则自动转换为0.00
	 * @param data　要转换的数据
	 * @return
	 */
	public static float getNotNullFloatValue(String data){
		float value = 0.0f;
		try {
			value = Float.parseFloat(data);
		}catch (NumberFormatException e){
			value = 0;
		}
		return value;
	}

	/**
	 * 取得一个非空的浮点数，如果提供的数据为空或者为字母，则返回returnValue
	 * @param date
	 * @param returnValue
	 * @return
	 */
	public static float getNotNullFloatValue(String date,float returnValue){
		float value = getNotNullFloatValue(date);
		if (value == 0.0f) {
			value = returnValue;
		}
		return value;
	}

	/**
	 * 如果参数为null,返回" "
	 * @return
	 */
	public static String getNullValueStr(String str){
		if(isNull(str)){
			return " ";
		}else{
			return str;
		}
	}

	/**
	 * 取得一个非空的浮点数，如果提供的数据为空或者为字母，则自动转换为0.00
	 * @param data　要转换的数据
	 * @return
	 */
	public static double getNotNullDoubleValue(String data){
		double value = 0.00;
		try {
			value = Double.parseDouble(data);
		}catch (NumberFormatException e){
			value = 0.00;
		}
		return value;
	}

	/**
	 * 取得一个非空的浮点数，如果提供的数据为空或者为字母，则返回returnValue
	 * @param date
	 * @param returnValue
	 * @return
	 */
	public static double getNotNullDoubleValue(String date,double returnValue){
		double value = getNotNullDoubleValue(date);
		if (value == 0.00) {
			value = returnValue;
		}
		return value;
	}

	/***
	 * 取目标STR指定开始字符串与结束字符串中间的字符串
	 * @param targetStr
	 * @param startStr
	 * @param endStr 可为"",为""表示截取从startStr以后的所有字符串
	 * @param isBlur 是否使用模糊截取
	 * @return
	 * @author lala 2008-10-24 下午03:50:09
	 */
	public static String getMiddleStr(String targetStr,String startStr,String endStr,boolean isBlur){
		String returnStr = targetStr;

		int startIndex = -1;
		if(isNotNull(startStr))
			startIndex = returnStr.indexOf(startStr);

		int endIndex = -1;
		if(isNotNull(endStr))
			endIndex = returnStr.indexOf(endStr);

		if(isBlur==false){
			/***************************************************
			 * 				精确截取							   *
			 ***************************************************/
			if(startIndex<0||endIndex<0){
				return "";
			}

			//确保结束字符一定在开始字符之后
			if(startIndex>endIndex){
				returnStr = returnStr.substring(startIndex+startStr.length(),returnStr.length());//先从开始的地方截取
				endIndex = returnStr.indexOf(endStr);
				if(endIndex<0)
					return "";
				returnStr = returnStr.substring(0,endIndex);
			}else{
				returnStr = returnStr.substring(startIndex+startStr.length(),endIndex);
			}
		}else{

			/***************************************************
			 * 				模糊截取							   *
			 ***************************************************/
			if(startIndex>endIndex){
				return "";
			}
			if(startIndex>-1)
				returnStr = returnStr.substring(startIndex+startStr.length(),returnStr.length());//先从开始的地方截取
			if(endIndex>-1)
				returnStr = returnStr.substring(0,endIndex);
		}



		return returnStr;
	}

	/***
	 * 精确截取
	 * 取目标STR指定开始字符串与结束字符串中间的字符串
	 * @param targetStr
	 * @param startStr
	 * @param endStr 可为"",为""表示截取从startStr以后的所有字符串
	 * @return
	 * @author lala 2008-10-24 下午03:50:09
	 */
	public static String getMiddleStr(String targetStr,String startStr,String endStr){
		return getMiddleStr(targetStr,startStr,endStr,false);
	}

	/**
	 * 取得hashMap中value的组合字符串，以'分隔
	 * 这是专门为扩展字段MAP提供的方法，否则扩展字段中出现英文逗号的
	 * 话会将一条数据分成两条数据
	 * @param map
	 * @return
	 */
	public static String getMapValueStrForExt(HashMap map){
		Iterator it = map.values().iterator();
		String over = "";

		while (it.hasNext()) {
			String value = (String) it.next();
			if(over.equals("")){
				over = value;
			}else{
				over = over + "@;@" + value;
			}
		}

		return over;
	}

	/**
	 * 取得一个非空的长整数，如果提供的数据为空或者为字母，则自动转换为0
	 * @param data　要转换的数据
	 * @return
	 */
	public static long getNotNullLongValue(String data){
		long value = 0;
		try {
			value = Long.parseLong(data);
		}catch (NumberFormatException e){
			value = 0;
		}
		return value;
	}

	/**
	 * 取得一个非空的长整数数，如果提供的数据为空或者为字母，则返回returnValue
	 * @param date
	 * @param returnValue
	 * @return
	 */
	public static long getNotNullLongValue(String date,long returnValue){
		long value = getNotNullLongValue(date);
		if (value == 0) {
			value = returnValue;
		}
		return value;
	}

	/**
	 * 对形如%D6%D0%B9%FA字符解码
	 * @param target
	 * @param enc
	 * @return
	 * @author lala 2009-12-31 下午03:25:17
	 */
	public static String decodeURI(String target,String enc){

		String returnStr = "";

		try {
			returnStr = java.net.URLDecoder.decode(target,enc);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}

		return returnStr;
	}

	/**
	 * 将一个字符型的Collection转换为一个字符串
	 * @param splitStr
	 * @return
	 * @author dekn  2011-8-29 下午02:35:25
	 */
	public static String strCollToStr(Collection strCollection,String splitStr){
		String str = "";
		if(null == strCollection || strCollection.size() <=0){
			return "";
		}

		Iterator it = strCollection.iterator();
		while (it.hasNext()) {
			String value = (String) it.next();
			if(str.length() > 0){
				str = str + "," + value;
			}else{
				str = value;
			}
		}

		return str;
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	public static String addZeroAtNumHead(String value){
		return addZeroAtNumHead(value,12);
	}



	/**
	 * 在数字字符的前面自动补0 例如100.69 转换后是00000000010069 100，转换后是00000000010000
	 * @param value
	 * @param length 补0后的整数位长度
	 * @return
	 */
	public static String addZeroAtNumHead(String value, int length){
		if(isNull(value)){
			return "";
		}

		int intLength = value.indexOf(".");	//整数位的长度
		//如果是浮点型数值 如90.00就转化为000000090.00，小于1的小数直接返回
		if(intLength>0){
			String str1 = value.substring(0,intLength);	//整数部分

			String str2	= value.substring(intLength+1);//小数部分

			/**
			 * 小数位的处理
			 * 不直接乘以100处理是为了不影响value的最大值……
			 */
			//如果str2是空 赋值为00
			if(isNull(str2)){
				str2="00";
			}
			//如果是1位小数 则在百分位上补0
			if(str2.length()==1){
				str2 = str2.substring(0,1)+"0";
				//大于等于两位小数的，只取前两位
			}else{
				str2 = str2.substring(0,2);
			}

			/**
			 * 整数位的处理
			 */
			//如果整数部分的长度大于需要的长度，则直接返回
			if(length<intLength){
				return value;
				//在整数位前面补0，再加上原来的小数位后返回
			}else{
				int cha = length-intLength;
				String zero = "";
				for(int i=0;i<cha;i++){
					zero+="0";
				}
				return zero+str1+str2;
			}
			//整形的数值
		}else{
			int valueLength = value.length();	//数字的长度
			if(length<valueLength){
				return value;
			}
			int cha = length-valueLength;
			String zero = "";
			for(int i=0;i<cha;i++){
				zero+="0";
			}
			return zero+value+"00";
		}

	}


	/**
	 * 给字符串添加小数点 精确到小数点后面两位
	 * @param value
	 * @return
	 */
	public static String addPoint(String value){
		return addPoint(value, 2);
	}


	/**
	 * 按照精度给字符串添加小数点
	 * @param value
	 * @param jd 精度
	 * @return
	 */
	public static String addPoint(String value, int jd){
		if(isNull(value)){
			return "";
		}

		if(value.indexOf(".")>=0){
			return value;
		}

		int length = value.length();

		if(length<jd){
			return value;
		}

		//按照精度，添加小数点
		String returnStr = value.substring(0,length-jd)+"."+value.substring(length-jd);

		return returnStr;
	}


	/**
	 * 转换为小写
	 * @param str
	 * @return
	 */
	public static String toLowerCase(String str){
		if(isNotNull(str)){
			return str.toLowerCase();
		}



		return "";
	}

	/**
	 *
	 * @param money 单位为分的整数
	 * @return 返回保留两位小数的字符串， 单位元
	 */
	public static double printMoney(int money) {
		int little = money % 100;
		return Double.parseDouble((money / 100 + "." + (little < 10 ? "0" + little : "" + little)));
	}

	/**
	 * 把字符串表示的金钱转换成单位为分的整数表示方法
	 * @param money
	 * @return
   */
	public static int convertToIntMoney(String money) {
		if (StrUtil.isNull(money)) {
			return 0;
		}
		return (int) StrUtil.getNotNullDoubleValue(MathUtil.multiply(money, "100"));
	}

	/**
	 * 把字符串表示的金钱转换成单位为分的整数表示方法
	 * @param money
	 * @return
   */
	public static int convertToIntMoney(double money) {
		return (int) (money * 100.0);
	}


}
