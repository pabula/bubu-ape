package com.pabula.db.Dialect.sql;

import com.pabula.common.util.DateUtil;


/**
 * SQL 方言	Oracle 9i
 * @author Dekn
 * www.cms4j.com	Nov 21, 2006 1:46:16 AM
 */
public class MSSQLDialect extends Dialect {
	
	
	
	public MSSQLDialect(){
		super();
		
		//选择数据SQL　　方言后的改进
		super.selectSQLForLimit = "SELECT ROW_NUMBER()Over($ORDER $DESC) as rowId,$SELECT_COLUMN FROM $TABLE $LEFT_JOIN $WHERE";
		
		//父类中，日期格式编码，采用了oracle的编码方式
		DATEFORMAT_CODE_YYYY = "yy";
		DATEFORMAT_CODE_YY 	= "yy";
		DATEFORMAT_CODE_MM 	= "mm";
		DATEFORMAT_CODE_DD 	= "dd";
		DATEFORMAT_CODE_HH 	= "hh";
		DATEFORMAT_CODE_HH24 = "hh";
		DATEFORMAT_CODE_MI 	= "mi";
		DATEFORMAT_CODE_SS 	= "ss";
		
		
		
		//当前日期方法   lx 20120207 注释
		//FUNCTION_NOW = "getdate()";		//mysql的方式
		FUNCTION_NOW = this.getFUNCTION_NOW();
		
		//关键字定义
		SQL_KEY_ALIAS = " as ";
	}
	
	
	
	/**
	 * 取得分页后的SQL
	 * @param sql
	 * @param hasOffset	是否为取前N条
	 * @param offset	开始
	 * @param limit	结束
	 * @return
	 */
	public String getLimitString(String sql, boolean hasOffset,int offset,int limit) {
		if(offset <= 0 && limit <= 0){
			return sql;
		}
		
		sql = sql.trim();
		
		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 );

		pagingSelect.append("Select * FROM ( ");
		pagingSelect.append(sql);
		pagingSelect.append(" ) as cms4jLimit where rowId between " + (offset+1) + " and " + (offset + limit));

		return pagingSelect.toString();
	}
	

	
	/**
	 * 获取取得一个时间片段的SQL
	 * @param time	如 2006-11-18
	 * @param foramt	如 yyyy-mm-dd
	 * @return 如果是oracle语言，则返回  to_date('2006-11-18','yyyy-mm-dd');
	 */
	public String getTimeFragment(String time,String format){
		return "'" + time + "'";
	}
	
	
	
	
	/**
	 * 获取日期比较表达式(MYSQL的dateFormat暂时只能用完整的描述，比如yyyy-mm　yyyy-mm-dd 不能用yy-mm)
	 * @param columnName	比较的字段
	 * @param compareOper	比较运算符
	 * @param compareValue	比较值
	 * @param dateFormat	日期格式	暂时只能用完整的描述，比如yyyy-mm　yyyy-mm-dd 不能用yy-mm
	 * @return
	 */
	public String getDateCompareString(String columnName,String compareOper,String compareValue,String dateFormat){
		
		//select convert(datetime,substring(convert(varchar,ADD_DATE,21),0,11)) as ttt from article 
		//SQL SERVER的格式转换比较特殊，并且substring必须为对象长度＋1才能正确获取相应长度的字符串
		//这种方法不再使用，因为无法将yyyy-dd格式的日期转换成datetime进行比较
		
		//实际使用下面的SQL来进行日期比较
		//select convert(CHAR(10),ADD_DATE,21) as ttt from article 
		
		return new StringBuffer(columnName.length() + 100).
				append(" convert(CHAR(").
				append(dateFormat.length()).
				append("),").
				append(columnName).
				append(",21) ").
				append(compareOper).
				append(" '").
				append(compareValue).
				append("'").
				toString();
	}
	
	
	
	/**
	 * LEFT 函数
	 * @param column
	 * @param left
	 * @return
	 */
	public String functionLeft(String column,int left){
		//SQL SERVER的格式转换比较特殊，并且substring必须为对象长度＋1才能正确获取相应长度的字符串
		
		return new StringBuffer(column.length() + 20).append(" substring(").append(column).append(",0,").append(left+1).append(")").toString();
	}
	
	/**
	 * LENGTH 函数
	 * @param column
	 * @return
	 */
	public String functionLength(String column){
		return "datalength(" + column + ")";
	}
	
	/**
	 * ToInt函数
	 */
	public String functionToInt(String columnName){
		return "cast(" + columnName + " as integer)";
	}
	
	/**
	 * add by lx 20120207
	 * 生成mssql 类型的数据库日期函数
	 */
	public  String getFUNCTION_NOW(){
		String date = DateUtil.getCurrentDay("yyyy-MM-dd HH-mm-ss");
		return getTimeFragment(date,FROMAT);
	}
	
	/**
	 * 随机取记录
	 * @author skayee
	 * @return 
	 */
	public String functionRandomByOrder(){
		return "newid()";
	}

}
