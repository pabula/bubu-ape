package com.pabula.db.Dialect.sql;

import com.pabula.common.util.DateUtil;
import com.pabula.fw.exception.DataAccessException;

import java.util.HashMap;


/**
 * SQL 方言
 * @author Dekn
 * www.cms4j.com	Nov 21, 2006 1:45:09 AM
 */
public class Dialect {
	

	//父类中，日期格式编码，采用了oracle的编码方式
	public String DATEFORMAT_CODE_YYYY;
	public String DATEFORMAT_CODE_YY;
	public String DATEFORMAT_CODE_MM;
	public String DATEFORMAT_CODE_DD;
	public String DATEFORMAT_CODE_HH;
	public String DATEFORMAT_CODE_HH24;
	public String DATEFORMAT_CODE_MI;
	public String DATEFORMAT_CODE_SS;
	
	//当前日期方法
	public String FUNCTION_NOW;
	
	
	//关键字定义
	public String SQL_KEY_ALIAS;
	
	//日期格式化 add by lx 20120207
	public String FROMAT ;
	
	
	//更新SQL模板
	public String updateSQL = 			"UPDATE $TABLE SET $UPDATE_COLUMN $WHERE";
	
	//删除SQL模板
	public String deleteSQL = 			"DELETE FROM $TABLE $WHERE";
	
	//插入数据SQL模板
	public String insertSQL = 			"INSERT INTO $TABLE ($INSERT_COLUMN_NAME) values ($INSERT_COLUMN_VALUE)";
	
	//选择数据SQL（标准的，不需要分页的）
	public String selectSQL = 			"SELECT $SELECT_COLUMN FROM $TABLE $LEFT_JOIN $WHERE $GROUP $HAVING $ORDER $DESC";
	
	//选择数据SQL（需要分页的）  mysql、oracle时，都与selectSQL相同，只有在为sqlserver2005时，才会有所不同
	public String selectSQLForLimit = 	"SELECT $SELECT_COLUMN FROM $TABLE $LEFT_JOIN $WHERE $GROUP $HAVING $ORDER $DESC";
	
	//获取数据SQL模板
	//String selectSQL = "SELECT $SELECT_COLUMN FROM $TABLE $WHERE $ORDER $DESC $LIMIT";
	//String selectSQL = "SELECT $SELECT_COLUMN FROM $TABLE $LEFT_JOIN $WHERE $ORDER $DESC $LIMIT";

	public static HashMap appConfigMap = new HashMap();
	
	public Dialect(){
		//父类中，日期格式编码，采用了oracle的编码方式
		DATEFORMAT_CODE_YYYY = "yyyy";
		DATEFORMAT_CODE_YY 	= "yy";
		DATEFORMAT_CODE_MM 	= "mm";
		DATEFORMAT_CODE_DD 	= "dd";
		DATEFORMAT_CODE_HH 	= "HH";
		DATEFORMAT_CODE_HH24 = "hh24";
		DATEFORMAT_CODE_MI 	= "mi";
		DATEFORMAT_CODE_SS 	= "ss";
		
		//mysql的日期格式化
		FROMAT = "%Y-%m-%d %H:%i:%s";
		//当前日期方法
		FUNCTION_NOW = getFUNCTION_NOW();
		//FUNCTION_NOW = "now()";		//mysql的方式
		
		//关键字定义
		SQL_KEY_ALIAS = " as ";
		
	}
	
	//abstract public DialectSuper getDialect() throws DataAccessException;
	/**
	 * add by lx 20120207
	 * 生成mysql 类型的数据库日期函数
	 */
	public  String getFUNCTION_NOW(){
		String date = DateUtil.getCurrentDay("yyyy-MM-dd HH:mm:ss");
		return getTimeFragment(date,FROMAT);
	}
	
	public static Dialect getDialect() throws DataAccessException {
    	
    	//方言类
    	//String dialectClassName = (String)appConfigMap.get("DATABASE_DIALECT_CLASS");
		String dialectClassName = "com.pabula.model.Dialect.sql.MysqlDialect";
    	
    	//初始化方言
		return initDialect( dialectClassName );
	}
//	
	
	public static Dialect getDialect(String dialectClassName) throws DataAccessException {
    	//初始化方言
		return initDialect( dialectClassName );
	}
	
	
	/**
	 * 初始化方言
	 * @param dialectClassName	方言类全名
	 * @return
	 * @throws DataAccessException
	 */
	public static Dialect initDialect(String dialectClassName) throws DataAccessException{
//		if ( dialectClassName == null ) {
//			throw new DataAccessException("请提供数据库方言的名称");
//		}
//		try {
//			return ( Dialect ) Class.forName(dialectClassName).newInstance();
//		}
//		catch ( ClassNotFoundException cnfe ) {
//			throw new DataAccessException( "未找到方言类: " + dialectClassName );
//		}
//		catch ( Exception e ) {
//			throw new DataAccessException( "无法初始化方言类", e );
//		}
		
		return new MysqlDialect();
	}
	
	
	
	
	public String getLimitString(String query, int offset, int limit) {
		return getLimitString( query, offset > 0,offset,limit );
	}
	
	
	/**
	 * 此方法需要具体的方言类实现
	 * @param sql
	 * @param hasOffset
	 * @param offset
	 * @param limit
	 * @return
	 */
	protected String getLimitString(String sql, boolean hasOffset,int offset,int limit) {
		throw new UnsupportedOperationException( "CMS4J： 此数据库方言，暂不支持分页  paged queries not supported" );
	}
	
	
	
	/**
	 * SQL中LIKE的方言实现
	 * @param columnName
	 * @param likevalue
	 * @return
	 */
	public String getLikeString(String columnName,String likevalue){
		StringBuffer sql = new StringBuffer( columnName.length()+100 );
		
		return sql.append(columnName).append(" LIKE '").append(likevalue).append("'").toString();
	}
	
	
	/**
	 * 获取日期比较表达式
	 * @param columnName	比较的字段
	 * @param compareOper	比较运算符
	 * @param compareValue	比较值
	 * @param dateFormat	日期格式
	 * @return
	 */
	public String getDateCompareString(String columnName,String compareOper,String compareValue,String dateFormat){
		throw new UnsupportedOperationException( "CMS4J： 此数据库方言，暂不支持 getDateCompareString 方法，以获取日期比较表达式" );
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
	 * LEFT 函数
	 * @param column
	 * @param left
	 * @return
	 */
	public String functionLeft(String column,int left){
		throw new UnsupportedOperationException( "CMS4J： 此数据库方言，暂不支持 Left 函数" );
	}
	
	/**
	 * LENGTH 函数
	 * @param column
	 * @return
	 */
	public String functionLength(String column){
		return "length(" + column + ")";
	}
	
	/**
	 * tochar函数
	 * @param column
	 * @return
	 * @author dekn   2007-11-13 下午09:38:52
	 */
	public String functionTochar(String column){
		return "" + column + "";
	}
	
	
	/**
	 * toInt函数
	 * @param column
	 * @return
	 */
	public String functionToInt(String column){
		return "" + column + "";
	}
	
	/**
	 * max函数
	 * @param column	字段名称
	 * @param as		别名
	 * @return 			max(COLUMN) as AS
	 */
	public String functionMax(String column,String as){
		return "max(" + column + ") " + SQL_KEY_ALIAS + as;
	}
	
	/**
	 * 随机取记录
	 * @author skayee
	 * @return 
	 */
	public String functionRandomByOrder(){
		return "rand()";
	}
	
}
