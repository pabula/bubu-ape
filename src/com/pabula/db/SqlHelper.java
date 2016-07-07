package com.pabula.db;

import com.pabula.common.util.DateUtil;
import com.pabula.common.util.ResourceManager;
import com.pabula.common.util.StrUtil;
import com.pabula.db.Dialect.sql.Dialect;
import com.pabula.fw.exception.DataAccessException;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 数据更新助手类
 * @author dekn     May 9, 2006 11:17:59 PM
 * 2006-07-09	Dekn	新增 LEFT JOIN 功能
 */
public class SqlHelper {

	Logger log = Logger.getLogger(SqlHelper.class);

	String TABLE = "";

	String WHERE = "";

	String LEFT_JOIN = "";

	String UPDATE_COLUMN = "";
	String SELECT_COLUMN = "";
	String INSERT_COLUMN_NAME = "";
	String INSERT_COLUMN_VALUE = "";

	String GROUP = "";	//2007-08-22	add by dekn

	String HAVING = "";	//2007-08-22	add by dekn

	String ORDER = "";

	String DESC = "";

	//取随机记录
	String RANDOM = "";//add by skayee 2012-12-29

	//String LIMIT = "";

	//分页起始位置
	private int offset = 0;

	//截取长度
	private int limit = 0;


	//已执行的SQL
	String executeedSql = "";


	public Dialect dialect;

	/********************************
	 * 预编译SQL                    **
	 ********************************/
	//是否使用预编译语句
	protected boolean isPrepareStmt;
	//存放字段的类型和值 类型和值存以一唯数组的形式添加在集合中
	//Collection filedValue = new ArrayList();

	String WHERE_PRE = "";
	String UPDATE_COLUMN_PRE = "";
	String INSERT_COLUMN_VALUE_PRE = "";

	Collection whereValue = new ArrayList();
	Collection updateColumnValue = new ArrayList();
	Collection insertColumnValue = new ArrayList();

	//匹配SQL表达式
	String regexWhereExpression="(.+?)\\s*(>=|<=|<>|>|=|<|is\\s*null|not\\s*null)\\s*(.+)";


	public SqlHelper() throws DataAccessException{
		try {
			//加载方言
			dialect = Dialect.getDialect();
		} catch (DataAccessException e) {
			throw e;
		}
	}


	public SqlHelper(String dialectClassName) throws DataAccessException{
		try {
			//加载方言
			dialect = Dialect.getDialect(dialectClassName);
		} catch (DataAccessException e) {
			throw e;
		}
	}

	/**
	 * 设置表格名称
	 * @param table
	 * @throws DataAccessException
	 */
	public void setTable(String table) throws DataAccessException{
		if(null == table || table.trim().equals("")){
			throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的表名称");
		}

		if(StrUtil.isNull(TABLE)){
			TABLE = removeLawlessStr(table);
		}else{
			TABLE = TABLE + "," + removeLawlessStr(table);
		}

	}

	/**
	 * 直接设置“表”的SQL
	 * @param tableSql
	 * @throws DataAccessException
	 * @author dekn   2009-9-11 上午10:45:53
	 */
	public void setTableSql(String tableSql, boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == tableSql || tableSql.trim().equals("")){
			throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的表名称");
		}

		if(isRemoveLawlessStr){
			TABLE = removeLawlessStr(tableSql);
		}else{
			TABLE = tableSql;
		}

	}

	/**
	 * 直接设置“表”的SQL
	 * @param tableSql
	 * @throws DataAccessException
	 * @author dekn   2009-9-11 上午10:45:53
	 */
	public void setTableSql(String tableSql) throws DataAccessException{
		this.setTableSql(tableSql,true);
	}



	/**
	 * 设置TAGBLE
	 * @param table	表名称
	 * @param alias	别名
	 * @throws DataAccessException
	 */
	public void setTable(String table,String alias) throws DataAccessException{
		if(StrUtil.isNull(alias)){
			setTable(table);
		}else{
			if(null == table || table.trim().equals("")){
				throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的表名称");
			}

			table = table + dialect.SQL_KEY_ALIAS + alias;

			if(StrUtil.isNull(TABLE)){
				TABLE = removeLawlessStr(table);
			}else{
				TABLE = TABLE + "," + removeLawlessStr(table);
			}

		}
	}




	/**
	 * 直接设置要更新的SQL
	 * @param sql
	 * @param isRemoveOld
	 * @throws DataAccessException
	 */
	public void setColumnForSQL(String sql,boolean isRemoveOld) throws DataAccessException{
		//判断是不是要进行拼接
		if(this.UPDATE_COLUMN.trim().length() > 0){
			if(isRemoveOld){
				this.UPDATE_COLUMN = sql;
			}else{
				this.UPDATE_COLUMN = this.UPDATE_COLUMN + "," + sql;
			}
		}else{
			this.UPDATE_COLUMN = sql;
		}


		if(this.isPrepareStmt){
			String[] field = sql.split(",");
			String sqlPre = "";

			for(int m=0;m<field.length;m++){
				//用正则表达式提取字段名以及字段值
				String[] nameOrValue = new String[2];;
				Pattern pattern=Pattern.compile(regexWhereExpression);
				Matcher matcher=pattern.matcher(field[m]);
				if(matcher.find()){
					String name = matcher.group(1);
					String value = matcher.group(3);
					nameOrValue[0] = name;
					nameOrValue[1] = value;
				}
				if(nameOrValue.length!=2){
					log.error("CMS4J SqlHelper异常：请提供合法的SET语句 @ setColumnForSQL(String sql,boolean isRemoveOld)");
					return;
				}

				if(sqlPre.length()==0){
					sqlPre = nameOrValue[0].trim() + " = ?";
				}else{
					sqlPre = sqlPre + "," + nameOrValue[0].trim() + " = ?";
				}
				updateColumnValue.add(StrUtil.replaceAll(nameOrValue[1].trim(), "'", ""));
			}

			if(this.UPDATE_COLUMN_PRE.trim().length() > 0){
				if(isRemoveOld){
					this.UPDATE_COLUMN_PRE = sqlPre;
				}else{
					this.UPDATE_COLUMN_PRE = this.UPDATE_COLUMN_PRE + "," + sqlPre;
				}
			}else{
				this.UPDATE_COLUMN_PRE = sqlPre;
			}
		}

	}

	/**
	 * 设置要更新的字段
	 * @param columnName	字段名称
	 * @param columnValue	字段值
	 * @throws DataAccessException
	 */
	public void setColumnForString(String columnName,String columnValue) throws DataAccessException{
		setColumnForString(columnName,columnValue,true);
	}



	/**
	 * 设置要更新的字段
	 * @param columnName
	 * @param columnValue
	 * @param datetimeFormat	datetimeFormat	日期值的格式描述 yyyy-mm-dd hh24:mi:ss		yy-mm-dd hh24:mi:ss	yyyy-mm-dd	yyyy-mm
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setColumnForDatetime(String columnName,String columnValue,String datetimeFormat, boolean isRemoveLawlessStr) throws DataAccessException{
		String sql = "";

		if(null == columnName || columnName.trim().equals("")){
			//throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的列名称");
			log.error("CMS4J SqlHelper异常：请提供合法的列名称 @ " + "setColumnForDatetime(String columnName,String columnValue,String datetimeFormat, boolean isRemoveLawlessStr)");
			return;
		}

		if(StrUtil.isNull(columnValue)){
			//根据不同的类型,设置column的SQL片段
			sql = columnName + " = null";

			//判断是不是要进行拼接
			if(this.UPDATE_COLUMN.trim().length() > 0){
				this.UPDATE_COLUMN = this.UPDATE_COLUMN + "," + sql;
			}else{
				this.UPDATE_COLUMN = sql;
			}

			if(this.isPrepareStmt){

				String sqlPre = columnName + " = ? ";

				//判断是不是要进行拼接
				if(this.UPDATE_COLUMN_PRE.trim().length() > 0){
					this.UPDATE_COLUMN_PRE = this.UPDATE_COLUMN_PRE + "," + sqlPre;
				}else{
					this.UPDATE_COLUMN_PRE = sqlPre;
				}

				updateColumnValue.add(null);
			}

			//throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的列值");
			//log.error("CMS4J SqlHelper异常：请提供合法的列值 @ " + "setColumnForDatetime(String columnName,String columnValue,String datetimeFormat, boolean isRemoveLawlessStr)");
			return;
		}

		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			columnValue = removeLawlessStr(columnValue);
		}

		if(StrUtil.isNotNull(datetimeFormat)){
			if(datetimeFormat.trim().equalsIgnoreCase("yyyy-mm-dd hh24:mi:ss")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YYYY + "-" +
								dialect.DATEFORMAT_CODE_MM + "-" +
								dialect.DATEFORMAT_CODE_DD + " " +
								dialect.DATEFORMAT_CODE_HH24 + "-" +
								dialect.DATEFORMAT_CODE_MI + "-" +
								dialect.DATEFORMAT_CODE_SS);
			}else if(datetimeFormat.trim().equalsIgnoreCase("yyyy-mm-dd")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YYYY + "-" +
								dialect.DATEFORMAT_CODE_MM + "-" +
								dialect.DATEFORMAT_CODE_DD);
			}else if(datetimeFormat.trim().equalsIgnoreCase("yy-mm-dd hh24:mi:ss")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YY + "-" +
								dialect.DATEFORMAT_CODE_MM + "-" +
								dialect.DATEFORMAT_CODE_DD + " " +
								dialect.DATEFORMAT_CODE_HH24 + "-" +
								dialect.DATEFORMAT_CODE_MI + "-" +
								dialect.DATEFORMAT_CODE_SS);
			}else if(datetimeFormat.trim().equalsIgnoreCase("yyyy-mm")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YYYY + "-" +
								dialect.DATEFORMAT_CODE_MM);
			}
		}

		//根据不同的类型,设置column的SQL片段
		sql = columnName + "=" + columnValue;

		//判断是不是要进行拼接
		if(this.UPDATE_COLUMN.trim().length() > 0){
			this.UPDATE_COLUMN = this.UPDATE_COLUMN + "," + sql;
		}else{
			this.UPDATE_COLUMN = sql;
		}


		if(this.isPrepareStmt){

			String sqlPre = columnName + " = ? ";

			//判断是不是要进行拼接
			if(this.UPDATE_COLUMN_PRE.trim().length() > 0){
				this.UPDATE_COLUMN_PRE = this.UPDATE_COLUMN_PRE + "," + sqlPre;
			}else{
				this.UPDATE_COLUMN_PRE = sqlPre;
			}

			updateColumnValue.add("@date_type_prefix@_"+StrUtil.replaceAll(columnValue, "'", ""));
		}
	}





	/**
	 * 设置要更新的字段
	 * @param columnName	字段名称
	 * @param columnValue	字段值
	 * @param isRemoveLawlessStr	是否要移除特殊字符,例如',从安全的角度考试,请设置此值为true
	 * @throws DataAccessException
	 */
	public void setColumnForString(String columnName,String columnValue,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的列名称 @ setColumnForString(String columnName,String columnValue,boolean isRemoveLawlessStr)");
			return;
			//throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的列名称");
		}

		if(null == columnValue){
			log.error("CMS4J SqlHelper异常：请提供合法的列值 @ setColumnForString(String columnName,String columnValue,boolean isRemoveLawlessStr)");
			return;
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的列值");
		}
		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			columnValue = removeLawlessStr(columnValue);
		}

		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = columnName + "='" + columnValue + "'";

		if(this.UPDATE_COLUMN.trim().length() > 0){
			this.UPDATE_COLUMN = this.UPDATE_COLUMN + "," + sql;
		}else{
			this.UPDATE_COLUMN = sql;
		}

		if(this.isPrepareStmt){
			String sqlPre = columnName + " = ? ";

			//判断是不是要进行拼接
			if(this.UPDATE_COLUMN_PRE.trim().length() > 0){
				this.UPDATE_COLUMN_PRE = this.UPDATE_COLUMN_PRE + "," + sqlPre;
			}else{
				this.UPDATE_COLUMN_PRE = sqlPre;
			}

			updateColumnValue.add(columnValue);
		}

	}


	/**
	 * 设置要更新的字段
	 * @param columnName	字段名称
	 * @param columnValue	字段值
	 * @throws DataAccessException
	 */
	public void setColumnForInt(String columnName,int columnValue) throws DataAccessException{
		setColumnForInt(columnName,columnValue,true);
	}

	/**
	 * 设置要更新的字段
	 * @param columnName	字段名称
	 * @param columnType	字段类型 int | string
	 * @param columnValue	字段值
	 * @param isRemoveLawlessStr	是否要移除特殊字符,例如',从安全的角度考试,请设置此值为true
	 * @throws DataAccessException
	 */
	public void setColumnForInt(String columnName,int columnValue,boolean isRemoveLawlessStr) throws DataAccessException{


		if(null == columnName || columnName.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的列名称 @ setColumnForInt(String columnName,int columnValue,boolean isRemoveLawlessStr)");
			return;
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的列名称");
		}

		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = columnName + "=" + columnValue;

		//判断是不是要进行拼接
		if(this.UPDATE_COLUMN.trim().length() > 0){
			this.UPDATE_COLUMN = this.UPDATE_COLUMN + "," + sql;
		}else{
			this.UPDATE_COLUMN = sql;
		}

		if(this.isPrepareStmt){
			String sqlPre = columnName + "= ? ";

			//判断是不是要进行拼接
			if(this.UPDATE_COLUMN_PRE.trim().length() > 0){
				this.UPDATE_COLUMN_PRE = this.UPDATE_COLUMN_PRE + "," + sqlPre;
			}else{
				this.UPDATE_COLUMN_PRE = sqlPre;
			}

			updateColumnValue.add(String.valueOf(columnValue));
		}

	}

	/**
	 * 设置要更新的字段
	 * @param columnName
	 * @param columnValue
	 * @throws DataAccessException
	 * @author dekn   2007-11-29 下午07:49:08
	 */
	public void setColumnForFloat(String columnName,float columnValue) throws DataAccessException{
		setColumnForFloat(columnName, columnValue,true);
	}


	/**
	 * 设置要更新的字段
	 * @param columnName
	 * @param columnValue
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 * @author dekn   2007-11-29 下午07:45:52
	 */
	public void setColumnForFloat(String columnName,float columnValue,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的列名称 @ setColumnForInt(String columnName,int columnValue,boolean isRemoveLawlessStr)");
			return;
		}

		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = columnName + "=" + columnValue;
		//判断是不是要进行拼接
		if(this.UPDATE_COLUMN.trim().length() > 0){
			this.UPDATE_COLUMN = this.UPDATE_COLUMN + "," + sql;
		}else{
			this.UPDATE_COLUMN = sql;
		}

		if(this.isPrepareStmt){
			String sqlPre = columnName + "= ? ";

			//判断是不是要进行拼接
			if(this.UPDATE_COLUMN_PRE.trim().length() > 0){
				this.UPDATE_COLUMN_PRE = this.UPDATE_COLUMN_PRE + "," + sqlPre;
			}else{
				this.UPDATE_COLUMN_PRE = sqlPre;
			}

			updateColumnValue.add(String.valueOf(columnValue));
		}
	}



	/**
	 * 设置WHERE条件
	 * @param columnName	条件字段名称
	 * @param oper 操作符
	 * @param columnValue	条件字段值
	 * @throws DataAccessException
	 */
	public void setWhereForString(String columnName,String oper, String columnValue) throws DataAccessException{
		setWhereForString(columnName,oper,columnValue, true);
	}

	/**
	 * 设置WHERE条件
	 * @param columnName	条件字段名称
	 * @param oper	操作符
	 * @param columnValue	条件字段值
	 * @param isRemoveLawlessStr	是否去除特殊符号
	 * @param isIgnoreNullValue		是否忽略空值
	 * @throws DataAccessException
	 */
	public void setWhereForString(String columnName,String oper, String columnValue,boolean isRemoveLawlessStr,boolean isIgnoreNullValue) throws DataAccessException{

		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			columnValue = removeLawlessStr(columnValue);
		}

		//如果要忽略空值，则进行判断值是否为空，如为空，则返回
		if(isIgnoreNullValue){
			if(null == columnValue || columnValue.trim().equals("")){
				return;
			}
		}

		setWhereForString(columnName,oper,columnValue, false);
	}

	/**
	 * 设置WHERE条件
	 * @param columnName	条件字段名称
	 * @param oper TODO
	 * @param columnValue	条件字段值
	 * @param isRemoveLawlessStr	是否去除特殊符号
	 * @param columnType	条件字段类型
	 * @throws DataAccessException
	 */
	public void setWhereForString(String columnName,String oper,String columnValue, boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
			//throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的条件列名称");
			log.error("CMS4J SqlHelper异常：请提供合法的条件列名称 @ setWhereForString(String columnName,String oper,String columnValue, boolean isRemoveLawlessStr)");
			return;
		}

		if(null == columnValue){
			log.info("CMS4J SqlHelper异常：提供的条件值为NULL，已忽略 " + columnName + " CurrWhereSQL: " + this.getCurrWhereSQL());
		}


		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			columnValue = removeLawlessStr(columnValue);
		}

		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = columnName + oper + "'" + columnValue + "'";

		//判断是不是要进行拼接
		if(this.WHERE.trim().length() > 0){
			this.WHERE = this.WHERE + " AND " + sql;
		}else{
			this.WHERE =  " WHERE " + sql;
		}

		if(this.isPrepareStmt){
			String sqlPre = columnName + oper + "?";

			//判断是不是要进行拼接
			if(this.WHERE_PRE.trim().length() > 0){
				this.WHERE_PRE = this.WHERE_PRE + " AND " + sqlPre;
			}else{
				this.WHERE_PRE =  " WHERE " + sqlPre;
			}

			whereValue.add(columnValue);
		}
	}

	/**
	 * 设置日期格式的WHERE条件
	 * @param columnName
	 * @param oper
	 * @param columnValue	为具体的日期值
	 * @param datetimeFormat	日期值的格式描述 yyyy-mm-dd hh24:mi:ss		yy-mm-dd hh24:mi:ss		yyyy-mm-dd	yyyy-mm
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setWhereForDatetime(String columnName,String oper,String columnValue,String datetimeFormat, boolean isRemoveLawlessStr) throws DataAccessException{
		this.setWhereForDatetime(columnName, oper, columnValue, datetimeFormat, isRemoveLawlessStr, false);
	}


	/**
	 * 设置日期格式的WHERE条件
	 * @param columnName
	 * @param oper
	 * @param columnValue	为具体的日期值
	 * @param datetimeFormat	日期值的格式描述 yyyy-mm-dd hh24:mi:ss		yy-mm-dd hh24:mi:ss		yyyy-mm-dd	yyyy-mm
	 * @param isRemoveLawlessStr
	 * @param isGetSqlFunction	是不是仅仅返回SQL片段，而不向整体sql中添加片段
	 * @throws DataAccessException
	 */
	public String setWhereForDatetime(String columnName,String oper,String columnValue,String datetimeFormat, boolean isRemoveLawlessStr,boolean isGetSqlFunction) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的条件列名称");
			log.error("CMS4J SqlHelper异常：请提供合法的条件列名称 @ setWhereForDatetime(String columnName,String oper,String columnValue,String datetimeFormat, boolean isRemoveLawlessStr)");
			return "";
		}

		if(null == columnValue){
			log.info("CMS4J SqlHelper异常：提供的条件值为NULL，已忽略 " + columnName + " CurrWhereSQL: " + this.getCurrWhereSQL());
		}


		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			columnValue = removeLawlessStr(columnValue);
		}

		String sql = "";

		if(StrUtil.isNotNull(datetimeFormat)){
			if(datetimeFormat.trim().equalsIgnoreCase("yyyy-mm-dd hh24:mi:ss")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YYYY + "-" +
								dialect.DATEFORMAT_CODE_MM + "-" +
								dialect.DATEFORMAT_CODE_DD+ " " +
								dialect.DATEFORMAT_CODE_HH24 + "-" +
								dialect.DATEFORMAT_CODE_MI + "-" +
								dialect.DATEFORMAT_CODE_SS);
			}else if(datetimeFormat.trim().equalsIgnoreCase("yyyy-mm-dd")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YYYY + "-" +
								dialect.DATEFORMAT_CODE_MM + "-" +
								dialect.DATEFORMAT_CODE_DD);
			}else if(datetimeFormat.trim().equalsIgnoreCase("yy-mm-dd hh24:mi:ss")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YY + "-" +
								dialect.DATEFORMAT_CODE_MM + "-" +
								dialect.DATEFORMAT_CODE_DD + " " +
								dialect.DATEFORMAT_CODE_HH24 + "-" +
								dialect.DATEFORMAT_CODE_MI + "-" +
								dialect.DATEFORMAT_CODE_SS);
			}else if(datetimeFormat.trim().equalsIgnoreCase("yyyy-mm")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YYYY + "-" +
								dialect.DATEFORMAT_CODE_MM);
			}

		}



		//根据不同的类型,设置column的SQL片段
		sql = columnName + oper + columnValue;

		if(!isGetSqlFunction){
			//判断是不是要进行拼接
			if(this.WHERE.trim().length() > 0){
				this.WHERE = this.WHERE + " AND " + sql;
			}else{
				this.WHERE =  " WHERE " + sql;
			}

			if(this.isPrepareStmt){
				String sqlPre = columnName + oper + "?";

				//判断是不是要进行拼接
				if(this.WHERE_PRE.trim().length() > 0){
					this.WHERE_PRE = this.WHERE_PRE + " AND " + sqlPre;
				}else{
					this.WHERE_PRE =  " WHERE " + sqlPre;
				}

				if(StrUtil.isNull(columnValue)){
					whereValue.add(null);
				}else{
					whereValue.add("@date_type_prefix@_"+StrUtil.replaceAll(columnValue,"'",""));
				}
			}
		}


		return sql;
	}



	/**
	 * 设置WHERE条件：日期比较
	 * @param columnName
	 * @param oper
	 * @param columnValue
	 * @param datetimeFormat
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setWhereForDateCompare(String columnName,String oper,String columnValue,String datetimeFormat, boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的条件列名称");
			log.error("CMS4J SqlHelper异常：请提供合法的条件列名称 @ setWhereForDateCompare(String columnName,String oper,String columnValue,String datetimeFormat, boolean isRemoveLawlessStr)");
			return;
		}

		if(null == columnValue){
			log.info("CMS4J SqlHelper异常：提供的条件值为NULL，已忽略 " + columnName + " CurrWhereSQL: " + this.getCurrWhereSQL());
		}


		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			columnValue = removeLawlessStr(columnValue);
		}


		String sql = "";

		sql = dialect.getDateCompareString(columnName,oper,columnValue,datetimeFormat);

		//判断是不是要进行拼接
		if(this.WHERE.trim().length() > 0){
			this.WHERE = this.WHERE + " AND " + sql;
		}else{
			this.WHERE =   " WHERE " + sql;
		}

		if(this.isPrepareStmt){
			String sqlPre = StrUtil.replaceAll(sql, columnValue, "?");

			//判断是不是要进行拼接
			if(this.WHERE_PRE.trim().length() > 0){
				this.WHERE_PRE = this.WHERE_PRE + " AND " + sqlPre;
			}else{
				this.WHERE_PRE =  " WHERE " + sqlPre;
			}

			whereValue.add(columnValue);
		}
	}


	/**
	 * 设置AND条件组
	 * @param column	字段名称
	 * @param operStr	操作符
	 * @param havaSplitStrValue	带半角逗号分隔符的值集合
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setAndGroupForInt(String column,String operStr,String havaSplitStrValue,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == column || column.trim().length()<=0 || null == havaSplitStrValue || havaSplitStrValue.trim().length() <= 0){
			return;
		}

		String[] array = havaSplitStrValue.split("\\,");
		for (int i = 0; i < array.length; i++) {
			array[i] = column + " " + operStr + array[i];	// 如 id = 1
		}

		this.setAndGroupForInt(array,isRemoveLawlessStr);
	}


	/**
	 * 设置and条件组
	 * @param orGroupArray
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setAndGroupForInt(String[] orGroupArray,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == orGroupArray || orGroupArray.length < 0){
			log.error("4JCOMMON SqlHelper异常：请提供合法的OR条件组数据 @ setOrGroupForInt(String[] orGroupArray,boolean isRemoveLawlessStr)");
			return;
			//throw new DataAccessException("4JCOMMON SqlHelper异常：请提供合法的OR条件组数据");
		}

		String orGroupStr = "";

		for (int i = 0; i < orGroupArray.length; i++) {
			//去除特殊字符,如'号
			if(isRemoveLawlessStr){
				orGroupArray[i] = removeLawlessStr(orGroupArray[i]);
			}

			if(orGroupStr.trim().length() > 0){
				orGroupStr = orGroupStr + " AND " + orGroupArray[i];
			}else{
				orGroupStr = orGroupArray[i];
			}
		}

		if(orGroupStr.trim().length() < 0){
			return;
		}

		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = "(" + orGroupStr + ")";

		//判断是不是要进行拼接
		if(this.WHERE.trim().length() > 0){
			this.WHERE = this.WHERE + " AND " + sql;
		}else{
			this.WHERE =  " WHERE " + sql;
		}

		if(this.isPrepareStmt){
			String orGroupStrPre = "";
			for (int i = 0; i < orGroupArray.length; i++) {
				//去除特殊字符,如'号
				if(isRemoveLawlessStr){
					orGroupArray[i] = removeLawlessStr(orGroupArray[i]);
				}

				//用正则表达式提取字段名以及字段值
				String[] nameOrValue = new String[2];;
				Pattern pattern=Pattern.compile(regexWhereExpression);
				Matcher matcher=pattern.matcher(orGroupArray[i]);
				if(matcher.find()){
					String name = matcher.group(1);
					String value = matcher.group(3);
					nameOrValue[0] = name;
					nameOrValue[1] = value;
				}
				if(nameOrValue.length!=2){
					log.error("4JCOMMON SqlHelper异常：请提供合法的And条件组数据 @ setAndGroupForInt(String[] orGroupArray,boolean isRemoveLawlessStr)");
					return;
				}

				if(orGroupStrPre.trim().length() > 0){
					orGroupStrPre = orGroupStrPre + " AND " + nameOrValue[0].trim() + " = ?";
				}else{
					orGroupStrPre = nameOrValue[0].trim() + " = ?";
				}

				whereValue.add(StrUtil.replaceAll(nameOrValue[1].trim(),"'",""));
			}

			if(orGroupStrPre.trim().length() < 0){
				return;
			}

			String sqlPre = "";

			//根据不同的类型,设置column的SQL片段
			sqlPre = "(" + orGroupStrPre + ")";

			//判断是不是要进行拼接
			if(this.WHERE_PRE.trim().length() > 0){
				this.WHERE_PRE = this.WHERE_PRE + " AND " + sqlPre;
			}else{
				this.WHERE_PRE =  " WHERE " + sqlPre;
			}
		}
	}

	/**
	 * 执行查询并取得某列的值（日期型）
	 * @param conn
	 * @param columnName
	 * @param action
	 * @return
	 * @throws DataAccessException
	 */
	public String selectAndGetDateValue(Connection conn,String columnName,String action) throws DataAccessException {

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();
			rs = select(conn,stmt,action);

			if(rs.next()){
				Timestamp str = rs.getTimestamp(columnName);
				return String.valueOf(rs.getTimestamp(columnName));
			}
		} catch (SQLException e) {
			throw new DataAccessException("[SqlHelper.selectAndGetDateValue 执行失败] " + action,e);
		}finally{
			ResourceManager.close(rs);
			ResourceManager.close(stmt);
			ResourceManager.close(conn);
		}

		return "";
	}
	/**
	 * 设置OR条件组
	 * @param column	字段名称
	 * @param operStr	操作符
	 * @param havaSplitStrValue	带半角逗号分隔符的值集合
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setOrGroupForString(String column,String operStr,String havaSplitStrValue,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == column || column.trim().length()<=0 || null == havaSplitStrValue || havaSplitStrValue.trim().length() <= 0){
			return;
		}

		if(isRemoveLawlessStr){
			column = removeLawlessStr(column);
			operStr = removeLawlessStr(operStr);
			havaSplitStrValue = removeLawlessStr(havaSplitStrValue);
		}

		String[] array = havaSplitStrValue.split("\\,");
		for (int i = 0; i < array.length; i++) {
			array[i] = column + " " + operStr + " '" + array[i] + "'";
		}

		this.setOrGroupForString(array,false);
	}


	/**
	 * 设置OR条件组
	 * @param column	字段名称
	 * @param operStr	操作符
	 * @param orGroupArray	OR条件组数据,格式 ["a","b"]
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setOrGroupForString(String column,String operStr,String[] orGroupArray,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == column || column.trim().length()<=0 || null == orGroupArray || orGroupArray.length <= 0){
			return;
		}

		if(isRemoveLawlessStr){
			column = removeLawlessStr(column);
			operStr = removeLawlessStr(operStr);
		}

		for (int i = 0; i < orGroupArray.length; i++) {
			orGroupArray[i] = removeLawlessStr(orGroupArray[i]);
			orGroupArray[i] = column + " " + operStr + " '" + orGroupArray[i] + "'";
		}

		this.setOrGroupForString(orGroupArray,false);
	}


	/**
	 * 设置OR条件组
	 * @param orGroupArray  OR条件组数据,格式 ["class_id = 'news'","class_id = 'tech'"]
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setOrGroupForString(String[] orGroupArray,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == orGroupArray || orGroupArray.length < 0){
			log.error("CMS4J SqlHelper异常：请提供合法的OR条件组数据 @ setOrGroupForString(String[] orGroupArray,boolean isRemoveLawlessStr)");
			return;
			//throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的OR条件组数据");
		}

		String orGroupStr = "";

		for (int i = 0; i < orGroupArray.length; i++) {
			//去除特殊字符,如'号
			if(isRemoveLawlessStr){
				orGroupArray[i] = removeLawlessStr(orGroupArray[i]);
			}

			if(orGroupStr.trim().length() > 0){
				orGroupStr = orGroupStr + " OR " + orGroupArray[i];
			}else{
				orGroupStr = orGroupArray[i];
			}
		}

		if(orGroupStr.trim().length() < 0){
			return;
		}


		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = "(" + orGroupStr + ")";

		//判断是不是要进行拼接
		if(this.WHERE.trim().length() > 0){
			this.WHERE = this.WHERE + " AND " + sql;
		}else{
			this.WHERE =  " WHERE " + sql;
		}


		if(this.isPrepareStmt){
			String orGroupStrPre = "";

			for (int i = 0; i < orGroupArray.length; i++) {
				//去除特殊字符,如'号
				if(isRemoveLawlessStr){
					orGroupArray[i] = removeLawlessStr(orGroupArray[i]);
				}

				//用正则表达式提取字段名以及字段值
				String[] nameOrValue = new String[2];;
				Pattern pattern=Pattern.compile(regexWhereExpression);
				Matcher matcher=pattern.matcher(orGroupArray[i]);
				if(matcher.find()){
					String name = matcher.group(1);
					String value = matcher.group(3);
					nameOrValue[0] = name;
					nameOrValue[1] = value;
				}
				if(nameOrValue.length!=2){
					log.error("CMS4J SqlHelper异常：请提供合法的OR条件组数据 @ setOrGroupForString(String[] orGroupArray,boolean isRemoveLawlessStr)");
					return;
				}

				if(orGroupStrPre.trim().length() > 0){
					orGroupStrPre = orGroupStrPre + " OR " + nameOrValue[0].trim() + " = ?";
				}else{
					orGroupStrPre = nameOrValue[0].trim() + " = ?";
				}
				whereValue.add(StrUtil.replaceAll(nameOrValue[1].trim(),"'",""));
			}

			if(orGroupStrPre.trim().length() < 0){
				return;
			}

			String sqlPre = "";

			//根据不同的类型,设置column的SQL片段
			sqlPre = "(" + orGroupStrPre + ")";

			//判断是不是要进行拼接
			if(this.WHERE_PRE.trim().length() > 0){
				this.WHERE_PRE = this.WHERE_PRE + " AND " + sqlPre;
			}else{
				this.WHERE_PRE =  " WHERE " + sqlPre;
			}
		}
	}

	/**
	 * 设置OR条件组
	 * @param orGroupArray  OR条件组数据,格式 ["class_id = 'news'","class_id = 'tech'"]
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setAndGroupForString(String[] orGroupArray,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == orGroupArray || orGroupArray.length < 0){
			log.error("CMS4J SqlHelper异常：请提供合法的OR条件组数据 @ setOrGroupForString(String[] orGroupArray,boolean isRemoveLawlessStr)");
			return;
			//throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的OR条件组数据");
		}

		String orGroupStr = "";

		for (int i = 0; i < orGroupArray.length; i++) {
			//去除特殊字符,如'号
			if(isRemoveLawlessStr){
				orGroupArray[i] = removeLawlessStr(orGroupArray[i]);
			}

			if(orGroupStr.trim().length() > 0){
				orGroupStr = orGroupStr + " AND " + orGroupArray[i];
			}else{
				orGroupStr = orGroupArray[i];
			}
		}

		if(orGroupStr.trim().length() < 0){
			return;
		}


		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = "(" + orGroupStr + ")";

		//判断是不是要进行拼接
		if(this.WHERE.trim().length() > 0){
			this.WHERE = this.WHERE + " AND " + sql;
		}else{
			this.WHERE =  " WHERE " + sql;
		}


		if(this.isPrepareStmt){
			String orGroupStrPre = "";

			for (int i = 0; i < orGroupArray.length; i++) {
				//去除特殊字符,如'号
				if(isRemoveLawlessStr){
					orGroupArray[i] = removeLawlessStr(orGroupArray[i]);
				}

				//用正则表达式提取字段名以及字段值
				String[] nameOrValue = new String[2];;
				Pattern pattern=Pattern.compile(regexWhereExpression);
				Matcher matcher=pattern.matcher(orGroupArray[i]);
				if(matcher.find()){
					String name = matcher.group(1);
					String value = matcher.group(3);
					nameOrValue[0] = name;
					nameOrValue[1] = value;
				}
				if(nameOrValue.length!=2){
					log.error("CMS4J SqlHelper异常：请提供合法的OR条件组数据 @ setOrGroupForString(String[] orGroupArray,boolean isRemoveLawlessStr)");
					return;
				}

				if(orGroupStrPre.trim().length() > 0){
					orGroupStrPre = orGroupStrPre + " OR " + nameOrValue[0].trim() + " = ?";
				}else{
					orGroupStrPre = nameOrValue[0].trim() + " = ?";
				}
				whereValue.add(StrUtil.replaceAll(nameOrValue[1].trim(),"'",""));
			}

			if(orGroupStrPre.trim().length() < 0){
				return;
			}

			String sqlPre = "";

			//根据不同的类型,设置column的SQL片段
			sqlPre = "(" + orGroupStrPre + ")";

			//判断是不是要进行拼接
			if(this.WHERE_PRE.trim().length() > 0){
				this.WHERE_PRE = this.WHERE_PRE + " AND " + sqlPre;
			}else{
				this.WHERE_PRE =  " WHERE " + sqlPre;
			}
		}
	}



	/**
	 * 设置OR条件组
	 * @param column	字段名称
	 * @param operStr	操作符
	 * @param havaSplitStrValue	带半角逗号分隔符的值集合
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setOrGroupForInt(String column,String operStr,String havaSplitStrValue,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == column || column.trim().length()<=0 || null == havaSplitStrValue || havaSplitStrValue.trim().length() <= 0){
			return;
		}

		String[] array = havaSplitStrValue.split("\\,");
		for (int i = 0; i < array.length; i++) {
			array[i] = column + " " + operStr + array[i];	// 如 id = 1
		}

		this.setOrGroupForInt(array,isRemoveLawlessStr);
	}


	/**
	 * 设置OR条件组
	 * @param orGroupArray  OR条件组数据,格式 ["class_id = 100","class_id = 101"]
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setOrGroupForInt(String[] orGroupArray,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == orGroupArray || orGroupArray.length < 0){
			log.error("CMS4J SqlHelper异常：请提供合法的OR条件组数据 @ setOrGroupForInt(String[] orGroupArray,boolean isRemoveLawlessStr)");
			return;
			//throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的OR条件组数据");
		}

		String orGroupStr = "";
		for (int i = 0; i < orGroupArray.length; i++) {
			//去除特殊字符,如'号
			if(isRemoveLawlessStr){
				orGroupArray[i] = removeLawlessStr(orGroupArray[i]);
			}

			if(orGroupStr.trim().length() > 0){
				orGroupStr = orGroupStr + " OR " + orGroupArray[i];
			}else{
				orGroupStr = orGroupArray[i];
			}
		}

		if(orGroupStr.trim().length() < 0){
			return;
		}


		String sql = "";
		//根据不同的类型,设置column的SQL片段
		sql = "(" + orGroupStr + ")";

		//判断是不是要进行拼接
		if(this.WHERE.trim().length() > 0){
			this.WHERE = this.WHERE + " AND " + sql;
		}else{
			this.WHERE =  " WHERE " + sql;
		}


		if(this.isPrepareStmt){
			String orGroupStrPre = "";
			for (int i = 0; i < orGroupArray.length; i++) {
				//去除特殊字符,如'号
				if(isRemoveLawlessStr){
					orGroupArray[i] = removeLawlessStr(orGroupArray[i]);
				}

				//用正则表达式提取字段名以及字段值
				String[] nameOrValue = new String[2];;
				Pattern pattern=Pattern.compile(regexWhereExpression);
				Matcher matcher=pattern.matcher(orGroupArray[i]);
				if(matcher.find()){
					String name = matcher.group(1);
					String value = matcher.group(3);
					nameOrValue[0] = name;
					nameOrValue[1] = value;
				}

				if(nameOrValue.length!=2){
					log.error("CMS4J SqlHelper异常：请提供合法的OR条件组数据 @ setOrGroupForInt(String[] orGroupArray,boolean isRemoveLawlessStr)");
					return;
				}

				if(orGroupStrPre.trim().length() > 0){
					orGroupStrPre = orGroupStrPre + " OR " + nameOrValue[0].trim() + " = ?";
				}else{
					orGroupStrPre = nameOrValue[0].trim() + " = ?";
				}
				whereValue.add(nameOrValue[1].trim());
			}

			if(orGroupStrPre.trim().length() < 0){
				return;
			}

			String sqlPre = "";
			//根据不同的类型,设置column的SQL片段
			sqlPre = "(" + orGroupStrPre + ")";

			//判断是不是要进行拼接
			if(this.WHERE_PRE.trim().length() > 0){
				this.WHERE_PRE = this.WHERE_PRE + " AND " + sqlPre;
			}else{
				this.WHERE_PRE =  " WHERE " + sqlPre;
			}
		}
	}



	/**
	 * 设置WHERE条件
	 * @param columnName	条件字段名称
	 * @param oper 操作符
	 * @param columnValue	条件字段值
	 * @throws DataAccessException
	 */
	public void setWhereForInt(String columnName,String oper, int columnValue) throws DataAccessException{
		setWhereForInt(columnName,oper,columnValue, true);
	}


	/**
	 * 设置WHERE条件
	 * @param columnName	条件字段名称
	 * @param oper 操作符
	 * @param columnValue	条件字段值
	 * @param ingoreNumber	可以被忽略的值
	 * @throws DataAccessException
	 */
	public void setWhereForInt(String columnName,String oper, int columnValue,int ingoreNumber) throws DataAccessException{

		//如果要忽略空值，则进行判断值是否为空，如为空，则返回
		if(columnValue == ingoreNumber){
			return;
		}

		setWhereForInt(columnName,oper,columnValue, true);
	}



	/**
	 * 设置WHERE条件
	 * @param columnName	条件字段名称
	 * @param oper TODO
	 * @param columnValue	条件字段值
	 * @param isRemoveLawlessStr	是否去除特殊符号
	 * @param columnType	条件字段类型
	 * @throws DataAccessException
	 */
	public void setWhereForInt(String columnName,String oper,int columnValue, boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的条件列名称");
			log.error("CMS4J SqlHelper异常：请提供合法的条件列名称 @ setWhereForInt(String columnName,String oper,int columnValue, boolean isRemoveLawlessStr)");
			return;
		}

		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = columnName + oper + columnValue;

		//判断是不是要进行拼接
		if(this.WHERE.trim().length() > 0){
			this.WHERE = this.WHERE + " AND " + sql;
		}else{
			this.WHERE = " WHERE " + sql;
		}

		if(this.isPrepareStmt){
			String	sqlPre = columnName + oper + "?";

			//判断是不是要进行拼接
			if(this.WHERE_PRE.trim().length() > 0){
				this.WHERE_PRE = this.WHERE_PRE + " AND " + sqlPre;
			}else{
				this.WHERE_PRE = " WHERE " + sqlPre;
			}

			whereValue.add(String.valueOf(columnValue));
		}
	}

	/**
	 * 设置WHERE条件
	 * @param columnName
	 * @param oper
	 * @param columnValue
	 * @throws DataAccessException
	 * @author dekn  2011-8-1 下午06:11:14
	 */
	public void setWhereForDecimal(String columnName,String oper,BigDecimal columnValue) throws DataAccessException{
		this.setWhereForDecimal(columnName, oper, columnValue, true);
	}

	/**
	 * 设置WHERE条件
	 * @param columnName
	 * @param oper
	 * @param columnValue
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 * @author dekn  2011-8-1 下午06:11:14
	 */
	public void setWhereForDecimal(String columnName,String oper,BigDecimal columnValue, boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的条件列名称");
			log.error("CMS4J SqlHelper异常：请提供合法的条件列名称 @ setWhereForDecimal(String columnName,String oper,BigDecimal columnValue, boolean isRemoveLawlessStr)");
			return;
		}

		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = columnName + oper + columnValue;

		//判断是不是要进行拼接
		if(this.WHERE.trim().length() > 0){
			this.WHERE = this.WHERE + " AND " + sql;
		}else{
			this.WHERE = " WHERE " + sql;
		}

		if(this.isPrepareStmt){
			String	sqlPre = columnName + oper + "?";

			//判断是不是要进行拼接
			if(this.WHERE_PRE.trim().length() > 0){
				this.WHERE_PRE = this.WHERE_PRE + " AND " + sqlPre;
			}else{
				this.WHERE_PRE = " WHERE " + sqlPre;
			}

			whereValue.add(String.valueOf(columnValue));
		}
	}



	/**
	 * 设置WHERE条件
	 * @param columnName	条件字段名称
	 * @param oper 操作符
	 * @param columnValue	条件字段值
	 * @throws DataAccessException
	 */
	public void setWhereForDouble(String columnName,String oper, double columnValue) throws DataAccessException{
		setWhereForDouble(columnName,oper,columnValue, true);
	}


	/**
	 * 设置WHERE条件
	 * @param columnName	条件字段名称
	 * @param oper 操作符
	 * @param columnValue	条件字段值
	 * @param ingoreNumber	可以被忽略的值
	 * @throws DataAccessException
	 */
	public void setWhereForDouble(String columnName,String oper, double columnValue,double ingoreNumber) throws DataAccessException{

		//如果要忽略空值，则进行判断值是否为空，如为空，则返回
		if(columnValue == ingoreNumber){
			return;
		}

		setWhereForDouble(columnName,oper,columnValue, true);
	}



	/**
	 * 设置WHERE条件
	 * @param columnName	条件字段名称
	 * @param oper TODO
	 * @param columnValue	条件字段值
	 * @param isRemoveLawlessStr	是否去除特殊符号
	 * @param columnType	条件字段类型
	 * @throws DataAccessException
	 */
	public void setWhereForDouble(String columnName,String oper,double columnValue, boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的条件列名称");
			log.error("CMS4J SqlHelper异常：请提供合法的条件列名称 @ setWhereForInt(String columnName,String oper,int columnValue, boolean isRemoveLawlessStr)");
			return;
		}

		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = columnName + oper + columnValue;

		//判断是不是要进行拼接
		if(this.WHERE.trim().length() > 0){
			this.WHERE = this.WHERE + " AND " + sql;
		}else{
			this.WHERE = " WHERE " + sql;
		}

		if(this.isPrepareStmt){
			String sqlPre = columnName + oper + "?";

			//判断是不是要进行拼接
			if(this.WHERE_PRE.trim().length() > 0){
				this.WHERE_PRE = this.WHERE_PRE + " AND " + sqlPre;
			}else{
				this.WHERE_PRE = " WHERE " + sqlPre;
			}

			whereValue.add(String.valueOf(columnValue));
		}
	}




	/**
	 * 设置SQL中的LIKE条件
	 * @param columnName
	 * @param likeValueStr
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setLike(String columnName,String likeValueStr, boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("") || null == likeValueStr || likeValueStr.trim().length() <= 0){
			return;
		}

		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = this.dialect.getLikeString(columnName,likeValueStr);

		//判断是不是要进行拼接
		if(this.WHERE.trim().length() > 0){
			this.WHERE = this.WHERE + " AND " + sql;
		}else{
			this.WHERE = " WHERE " + sql;
		}

		if(this.isPrepareStmt){
			String sqlPre = this.dialect.getLikeString(columnName,likeValueStr);
			sqlPre = StrUtil.replaceAll(sqlPre, likeValueStr, "?");

			//判断是不是要进行拼接
			if(this.WHERE_PRE.trim().length() > 0){
				this.WHERE_PRE = this.WHERE_PRE + " AND " + sqlPre;
			}else{
				this.WHERE_PRE = " WHERE " + sqlPre;
			}

			whereValue.add(likeValueStr);
		}
	}

	/**
	 * 设置SQL中的LIKE条件
	 * @param columnName
	 * @param likeValueStr
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setOrLike(String columnName,String likeValueStr, boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("") || null == likeValueStr || likeValueStr.trim().length() <= 0){
			return;
		}

		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = this.dialect.getLikeString(columnName,likeValueStr);

		//判断是不是要进行拼接
		if(this.WHERE.trim().length() > 0){
			this.WHERE = this.WHERE + " OR " + sql;
		}else{
			this.WHERE = " WHERE " + sql;
		}

		if(this.isPrepareStmt){
			String sqlPre = this.dialect.getLikeString(columnName,likeValueStr);
			sqlPre = StrUtil.replaceAll(sqlPre, likeValueStr, "?");

			//判断是不是要进行拼接
			if(this.WHERE_PRE.trim().length() > 0){
				this.WHERE_PRE = this.WHERE_PRE + " OR " + sqlPre;
			}else{
				this.WHERE_PRE = " WHERE " + sqlPre;
			}

			whereValue.add(likeValueStr);
		}
	}




	/**
	 * 获取当前的WHERE条件
	 * @return
	 */
	public String getCurrWhereSQL(){
		return this.WHERE;
	}

	/**
	 * 取得当前的TABLE
	 * @return
	 * @author dekn   2009-9-11 上午10:39:18
	 */
	public String getCurrTableSql(){
		return this.TABLE;
	}

	/**
	 * 取得当前的SELECT_COLUMN
	 * @return
	 * @author dekn   2009-9-11 上午10:39:56
	 */
	public String getCurrSelectColumnSql(){
		return this.SELECT_COLUMN;
	}

	/**
	 * 取得当前的 LEFT_JOIN
	 * @return
	 * @author dekn   2009-9-11 上午10:55:08
	 */
	public String getCurrLeftJoinSql(){
		return this.LEFT_JOIN;
	}

	/**
	 * 设置当前的WHERE条件为指定的字符串
	 * @param str
	 * @param isCleanOldWhere 是否清楚老的WHERE条件
	 */
	public void setWhereSQL(String str,boolean isCleanOldWhere){
		if(StrUtil.isNull(str)){
			return;
		}

		if(!isCleanOldWhere){
			if(this.WHERE.trim().length() > 0){
				this.WHERE = this.WHERE + " AND " + str;
			}else{
				this.WHERE = " WHERE " + str;
			}
		}else{
			this.WHERE = this.WHERE + str;
		}

		if(this.isPrepareStmt){
			String sqlPre = "";
			Pattern pattern=Pattern.compile(regexWhereExpression);
			Matcher matcher=pattern.matcher(str);

			while(matcher.find()){
				String[] nameOrValue = { matcher.group(1),matcher.group(3) };
				if(nameOrValue.length!=2){
					log.error("CMS4J SqlHelper异常：请提供合法的WHERE条件 @ setWhereSQL(String str,boolean isCleanOldWhere)");
					return;
				}

				if(sqlPre.length() > 0){
					sqlPre = sqlPre + " AND " + nameOrValue[0] + " = ? ";
				}else{
					sqlPre = " WHERE " + nameOrValue[0] + " = ? ";
				}
				whereValue.add(StrUtil.replaceAll(nameOrValue[1].trim(),"'",""));
			}

			if(!isCleanOldWhere){
				if(this.WHERE_PRE.trim().length() > 0){
					this.WHERE_PRE = this.WHERE_PRE + " AND " + sqlPre;
				}else{
					this.WHERE_PRE = " WHERE " + sqlPre;
				}
			}else{
				this.WHERE_PRE = " WHERE " + sqlPre;
			}
		}

	}


	/**
	 * 设置INSERT语句中的字段名与值
	 * @param columnName	字段名称
	 * @param columnValue	字段值
	 * @throws DataAccessException
	 */
	public void setInsertForString(String columnName,String columnValue) throws DataAccessException{
		setInsertForString(columnName,columnValue,true);
	}



	/**
	 * 设置INSERT语句中的字段名与值
	 * @param columnName	字段名称
	 * @param columnValue	字段值
	 * @param isRemoveLawlessStr	是否去除特殊符号
	 * @throws DataAccessException
	 */
	public void setInsertForString(String columnName,String columnValue,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的列名称 @ setInsertForString(String columnName,String columnValue,boolean isRemoveLawlessStr)");
			return;
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的列名称");
		}

		if(null == columnValue){
			//log.error("CMS4J SqlHelper异常：请提供合法的列值 @ setInsertForString(String columnName,String columnValue,boolean isRemoveLawlessStr)");
			return;
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的列值");
		}


		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			columnValue = removeLawlessStr(columnValue);
		}

		String insertColumnValue = "";

		insertColumnValue = "'" + columnValue + "'";

		//判断是不是要进行拼接
		if(this.INSERT_COLUMN_NAME.trim().length() > 0){
			//拼接 INSERT_COLUMN_NAME 与 INSERT_COLUMN_VALUE
			this.INSERT_COLUMN_NAME = this.INSERT_COLUMN_NAME + " , " + columnName;

			this.INSERT_COLUMN_VALUE = this.INSERT_COLUMN_VALUE + " , " + insertColumnValue;
		}else{
			//拼接 INSERT_COLUMN_NAME 与 INSERT_COLUMN_VALUE
			this.INSERT_COLUMN_NAME = columnName;

			this.INSERT_COLUMN_VALUE = insertColumnValue;
		}

		if(this.isPrepareStmt){
			//判断是不是要进行拼接
			if(this.INSERT_COLUMN_VALUE_PRE.trim().length() > 0){
				this.INSERT_COLUMN_VALUE_PRE = this.INSERT_COLUMN_VALUE_PRE + " , ?";
			}else{
				this.INSERT_COLUMN_VALUE_PRE = "?";
			}

			this.insertColumnValue.add(columnValue);
		}
	}

	/**
	 * 插入一个默认格式的日期型值
	 * @param columnName
	 * @param columnValue
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 * @author dekn   2007-11-13 下午08:23:47
	 */
	public void setInsertForDatetime(String columnName,String columnValue,boolean isRemoveLawlessStr) throws DataAccessException{
		this.setInsertForDatetime(columnName, columnValue, "yyyy-mm-dd hh24:mi:ss",isRemoveLawlessStr);
	}


	/**
	 * 插入一个日期型的字段值
	 * @param columnName
	 * @param columnValue
	 * @param datetimeFormat
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 * @author dekn   2007-11-13 下午08:23:20
	 */
	public void setInsertForDatetime(String columnName,String columnValue,String datetimeFormat, boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的列名称 @ setInsertForString(String columnName,String columnValue,boolean isRemoveLawlessStr)");
			return;
		}

		if(null == columnValue){
			return;
		}


		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			columnValue = removeLawlessStr(columnValue);
		}

		if(StrUtil.isNotNull(datetimeFormat)){
			if(datetimeFormat.trim().equalsIgnoreCase("yyyy-mm-dd hh24:mi:ss")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YYYY + "-" +
								dialect.DATEFORMAT_CODE_MM + "-" +
								dialect.DATEFORMAT_CODE_DD + " " +
								dialect.DATEFORMAT_CODE_HH24 + "-" +
								dialect.DATEFORMAT_CODE_MI + "-" +
								dialect.DATEFORMAT_CODE_SS);
			}else if(datetimeFormat.trim().equalsIgnoreCase("yyyy-mm-dd")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YYYY + "-" +
								dialect.DATEFORMAT_CODE_MM + "-" +
								dialect.DATEFORMAT_CODE_DD);
			}else if(datetimeFormat.trim().equalsIgnoreCase("yy-mm-dd hh24:mi:ss")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YY + "-" +
								dialect.DATEFORMAT_CODE_MM + "-" +
								dialect.DATEFORMAT_CODE_DD + " " +
								dialect.DATEFORMAT_CODE_HH24 + "-" +
								dialect.DATEFORMAT_CODE_MI + "-" +
								dialect.DATEFORMAT_CODE_SS);
			}else if(datetimeFormat.trim().equalsIgnoreCase("yyyy-mm")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YYYY + "-" +
								dialect.DATEFORMAT_CODE_MM);
			}

		}

		//判断是不是要进行拼接
		if(this.INSERT_COLUMN_NAME.trim().length() > 0){
			//拼接 INSERT_COLUMN_NAME 与 INSERT_COLUMN_VALUE
			this.INSERT_COLUMN_NAME = this.INSERT_COLUMN_NAME + " , " + columnName;

			this.INSERT_COLUMN_VALUE = this.INSERT_COLUMN_VALUE + " , " + columnValue;
		}else{
			//拼接 INSERT_COLUMN_NAME 与 INSERT_COLUMN_VALUE
			this.INSERT_COLUMN_NAME = columnName;

			this.INSERT_COLUMN_VALUE = columnValue;
		}


		if(this.isPrepareStmt){
			//判断是不是要进行拼接
			if(this.INSERT_COLUMN_VALUE_PRE.trim().length() > 0){
				this.INSERT_COLUMN_VALUE_PRE = this.INSERT_COLUMN_VALUE_PRE + " , ?";
			}else{
				this.INSERT_COLUMN_VALUE_PRE = "?";
			}

			insertColumnValue.add("@date_type_prefix@_"+StrUtil.replaceAll(columnValue, "'", ""));
		}
	}



	/**
	 * 设置INSERT语句中的字段名与值
	 * @param columnName	字段名称
	 * @param columnValue	字段值
	 * @throws DataAccessException
	 */
	public void setInsertForInt(String columnName,int columnValue) throws DataAccessException{
		setInsertForInt(columnName,columnValue,true);
	}



	/**
	 * 设置INSERT语句中的字段名与值
	 * @param columnName	字段名称
	 * @param columnValue	字段值
	 * @param isRemoveLawlessStr	是否去除特殊符号
	 * @throws DataAccessException
	 */
	public void setInsertForInt(String columnName,int columnValue,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的列名称 @ setInsertForInt(String columnName,int columnValue,boolean isRemoveLawlessStr)");
			return;
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的列名称");
		}


		//判断是不是要进行拼接
		if(this.INSERT_COLUMN_NAME.trim().length() > 0){
			//拼接 INSERT_COLUMN_NAME 与 INSERT_COLUMN_VALUE
			this.INSERT_COLUMN_NAME = this.INSERT_COLUMN_NAME + " , " + columnName;

			this.INSERT_COLUMN_VALUE = this.INSERT_COLUMN_VALUE + " , " + columnValue;
		}else{
			//拼接 INSERT_COLUMN_NAME 与 INSERT_COLUMN_VALUE
			this.INSERT_COLUMN_NAME = columnName;

			this.INSERT_COLUMN_VALUE = "" + columnValue;
		}

		if(this.isPrepareStmt){
			//判断是不是要进行拼接
			if(this.INSERT_COLUMN_VALUE_PRE.trim().length() > 0){
				this.INSERT_COLUMN_VALUE_PRE = this.INSERT_COLUMN_VALUE_PRE + " , ?";
			}else{
				this.INSERT_COLUMN_VALUE_PRE = "?";
			}

			insertColumnValue.add(String.valueOf(columnValue));
		}
	}

	/**
	 *  插入一个浮点型的值
	 * @param columnName
	 * @param columnValue
	 * @throws DataAccessException
	 * @author dekn   2007-11-29 下午07:49:40
	 */
	public void setInsertForFloat(String columnName,float columnValue) throws DataAccessException{
		setInsertForFloat(columnName, columnValue,true);
	}


	/**
	 * 插入一个浮点型的值
	 * @param columnName
	 * @param columnValue
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 * @author dekn   2007-11-29 下午07:44:49
	 */
	public void setInsertForFloat(String columnName,float columnValue,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的列名称 @ setInsertForInt(String columnName,int columnValue,boolean isRemoveLawlessStr)");
			return;
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的列名称");
		}


		//判断是不是要进行拼接
		if(this.INSERT_COLUMN_NAME.trim().length() > 0){
			//拼接 INSERT_COLUMN_NAME 与 INSERT_COLUMN_VALUE
			this.INSERT_COLUMN_NAME = this.INSERT_COLUMN_NAME + " , " + columnName;

			this.INSERT_COLUMN_VALUE = this.INSERT_COLUMN_VALUE + " , " + columnValue;
		}else{
			//拼接 INSERT_COLUMN_NAME 与 INSERT_COLUMN_VALUE
			this.INSERT_COLUMN_NAME = columnName;

			this.INSERT_COLUMN_VALUE = "" + columnValue;
		}

		if(this.isPrepareStmt){
			//判断是不是要进行拼接
			if(this.INSERT_COLUMN_VALUE_PRE.trim().length() > 0){
				this.INSERT_COLUMN_VALUE_PRE = this.INSERT_COLUMN_VALUE_PRE + " , ?";
			}else{
				this.INSERT_COLUMN_VALUE_PRE = "?";
			}

			insertColumnValue.add(String.valueOf(columnValue));
		}
	}


	/**
	 * 插入一个数值型的值
	 * @param columnName
	 * @param columnValue
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 * @author dekn  2011-8-1 下午06:05:43
	 */
	public void setInsertForDecimal(String columnName,BigDecimal columnValue) throws DataAccessException{
		setInsertForDecimal(columnName,columnValue,true);
	}

	/**
	 * 插入一个数值型的值
	 * @param columnName
	 * @param columnValue
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 * @author dekn  2011-8-1 下午06:05:43
	 */
	public void setInsertForDecimal(String columnName,BigDecimal columnValue,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的列名称 @ setInsertForDecimal(String columnName,BigDecimal columnValue,boolean isRemoveLawlessStr)");
			return;
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的列名称");
		}


		//判断是不是要进行拼接
		if(this.INSERT_COLUMN_NAME.trim().length() > 0){
			//拼接 INSERT_COLUMN_NAME 与 INSERT_COLUMN_VALUE
			this.INSERT_COLUMN_NAME = this.INSERT_COLUMN_NAME + " , " + columnName;

			this.INSERT_COLUMN_VALUE = this.INSERT_COLUMN_VALUE + " , " + columnValue;
		}else{
			//拼接 INSERT_COLUMN_NAME 与 INSERT_COLUMN_VALUE
			this.INSERT_COLUMN_NAME = columnName;

			this.INSERT_COLUMN_VALUE = "" + columnValue;
		}

		if(this.isPrepareStmt){
			//判断是不是要进行拼接
			if(this.INSERT_COLUMN_VALUE_PRE.trim().length() > 0){
				this.INSERT_COLUMN_VALUE_PRE = this.INSERT_COLUMN_VALUE_PRE + " , ?";
			}else{
				this.INSERT_COLUMN_VALUE_PRE = "?";
			}

			insertColumnValue.add(String.valueOf(columnValue));
		}
	}


	/**
	 * 设置选择数据时的 COLUMN，注意，这个COLUMN可以是一个完整的字段集合或表达式
	 * @param columnNames
	 * @throws DataAccessException
	 */
	public void setSelectColumn(String columnNames) throws DataAccessException{
		this.setSelectColumn(columnNames, "", true);
	}

	/**
	 * 设置COLUMN的SQL
	 * @param columnNamesSql
	 * @author dekn   2009-9-11 上午10:49:16
	 */
	public void setSelectColumnSql(String columnNamesSql){
		if(null == columnNamesSql || columnNamesSql.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的列名称 @ setSelectColumnSql(String columnNamesSql)");
			return;
		}

		this.SELECT_COLUMN = columnNamesSql;
	}



	/**
	 * 设置选择数据时的 COLUMN，注意，这个COLUMN可以是一个完整的字段集合或表达式
	 * @param columnNames
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setSelectColumn(String columnNames,boolean isRemoveLawlessStr) throws DataAccessException{
		this.setSelectColumn(columnNames, "", isRemoveLawlessStr);
	}


	/**
	 * 设置选择的字段
	 * @param columnNames	字段名称
	 * @param alias	别名
	 * @param isRemoveLawlessStr 是否移除特殊字符
	 * @throws DataAccessException
	 * @author dekn   2007-11-4 下午05:33:46
	 */
	public void setSelectColumn(String columnNames,String alias,boolean isRemoveLawlessStr) throws DataAccessException{
		this.setSelectColumn(columnNames, alias, isRemoveLawlessStr, false);
	}

	/**
	 * 设置选择的字段
	 * @param columnNames	字段名称
	 * @param alias	别名
	 * @param isRemoveLawlessStr 是否移除特殊字符
	 * @param isAdd	是否为新添加选择的字段，之前的不删除
	 * @throws DataAccessException
	 * @author dekn  2011-7-22 上午12:16:37
	 */
	public void setSelectColumn(String columnNames,String alias,boolean isRemoveLawlessStr,boolean isAdd) throws DataAccessException{
		if(null == columnNames || columnNames.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的列名称 @ setSelectColumn(String columnNames,boolean isRemoveLawlessStr)");
			return;
		}

		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			columnNames = removeLawlessStr(columnNames);
		}

		//如果设置了别名
		if(StrUtil.isNotNull(alias)){
			if(isAdd && StrUtil.isNotNull(this.SELECT_COLUMN)){
				this.SELECT_COLUMN = this.SELECT_COLUMN + "," + columnNames + dialect.SQL_KEY_ALIAS + alias;
			}else{
				this.SELECT_COLUMN = columnNames + dialect.SQL_KEY_ALIAS + alias;
			}
		}else{
			if(isAdd && StrUtil.isNotNull(this.SELECT_COLUMN)){
				this.SELECT_COLUMN = this.SELECT_COLUMN + "," + columnNames;
			}else{
				this.SELECT_COLUMN = columnNames;
			}
		}
	}


	/**
	 * 设置查询的LEFT JOIN
	 * @param leftJoinTable
	 * @param on
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setLeftJoin(String leftJoinTable,String on,boolean isRemoveLawlessStr) throws DataAccessException{
		if(StrUtil.isNull(leftJoinTable) || StrUtil.isNull(on)){
			log.error("CMS4J SqlHelper异常：请提供合法的LEFT JOIN参数! @ setLeftJoin(String leftJoinTable,String on,boolean isRemoveLawlessStr)");
			return;
//			throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的LEFT JOIN参数!");
		}

		String joinStr = "LEFT JOIN " + leftJoinTable + " ON " + on;

		if(isRemoveLawlessStr){
			joinStr = removeLawlessStr(joinStr);
		}

		//判断是不是要进行拼接
		if(this.LEFT_JOIN.trim().length() > 0){
			this.LEFT_JOIN = this.LEFT_JOIN + "  " + joinStr;
		}else{
			this.LEFT_JOIN = joinStr;
		}
	}

	/**
	 * 设置当前的left join SQL
	 * @param leftJoinSql
	 * @author dekn   2009-9-11 上午10:55:39
	 */
	public void setLeftJoinSql(String leftJoinSql){
		this.LEFT_JOIN = removeLawlessStr(leftJoinSql);
	}

	/**
	 * 设置查询的LEFT JOIN
	 * @param leftJoinTable
	 * @param alias
	 * @param on
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setLeftJoin(String leftJoinTable,String alias,String on,boolean isRemoveLawlessStr) throws DataAccessException{
		if(StrUtil.isNull(leftJoinTable) || StrUtil.isNull(on)){
			log.error("CMS4J SqlHelper异常：请提供合法的LEFT JOIN参数 @ setLeftJoin(String leftJoinTable,String alias,String on,boolean isRemoveLawlessStr)");
			return;
//			throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的LEFT JOIN参数!");
		}

		if(StrUtil.isNotNull(alias)){
			leftJoinTable = leftJoinTable + dialect.SQL_KEY_ALIAS + alias;
		}


		String joinStr = "LEFT JOIN " + leftJoinTable + " ON " + on;

		if(isRemoveLawlessStr){
			joinStr = removeLawlessStr(joinStr);
		}

		//判断是不是要进行拼接
		if(this.LEFT_JOIN.trim().length() > 0){
			this.LEFT_JOIN = this.LEFT_JOIN + "  " + joinStr;
		}else{
			this.LEFT_JOIN = joinStr;
		}
	}

	/**
	 * 设置查询的LEFT JOIN
	 * @param leftJoinTable 表名称
	 * @param alias 数据表别名
	 * @param column1 字段1
	 * @param column2 字段2
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 * @author qlf
	 * @datetime 2012-11-2 下午02:43:31
	 */
	public void setLeftJoin(String leftJoinTable,String alias,String column1,String column2,boolean isRemoveLawlessStr) throws DataAccessException{
		if(StrUtil.isNull(leftJoinTable) || StrUtil.isNull(column1) || StrUtil.isNull(column2)){
			log.error("CMS4J SqlHelper异常：请提供合法的LEFT JOIN参数 @ setLeftJoin(String leftJoinTable,String alias,String on,boolean isRemoveLawlessStr)");
			return;
		}

		if(StrUtil.isNotNull(alias)){
			leftJoinTable = leftJoinTable + dialect.SQL_KEY_ALIAS + alias;
		}


		String joinStr = "LEFT JOIN " + leftJoinTable + " ON " + column1 + " = " + dialect.functionTochar(column2);

		if(isRemoveLawlessStr){
			joinStr = removeLawlessStr(joinStr);
		}

		//判断是不是要进行拼接
		if(this.LEFT_JOIN.trim().length() > 0){
			this.LEFT_JOIN = this.LEFT_JOIN + "  " + joinStr;
		}else{
			this.LEFT_JOIN = joinStr;
		}
	}



	/**
	 * 设置查询的LEFT JOIN
	 * @param leftJoinTable
	 * @param alias
	 * @param on
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setLeftJoin(String leftJoinSql,boolean isRemoveLawlessStr) throws DataAccessException{
		if(StrUtil.isNull(leftJoinSql)){
			return;
		}

		String joinStr = " " + leftJoinSql;

		if(isRemoveLawlessStr){
			joinStr = removeLawlessStr(joinStr);
		}

		//判断是不是要进行拼接
		if(this.LEFT_JOIN.trim().length() > 0){
			this.LEFT_JOIN = this.LEFT_JOIN + "  " + joinStr;
		}else{
			this.LEFT_JOIN = joinStr;
		}
	}





	/**
	 * 获取当前的WHERE条件
	 * @return
	 */
	public String getCurrLeftJoin(){
		return this.LEFT_JOIN;
	}



	/**
	 * 设置排序
	 * @param order
	 * @throws DataAccessException
	 */
	public void setORDER(String order) throws DataAccessException{
		if(null == order || order.trim().equals("")){
			return;
		}

		setORDER(order,true);
	}


	/**
	 * 设置排序
	 * @param order 排序字段，可以是表达式
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setORDER(String order,boolean isRemoveLawlessStr) throws DataAccessException{

		if(null == order || order.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的排序列名称 @ setORDER(String order,boolean isRemoveLawlessStr)");
			return;
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的排序列名称");
		}

//		if(this.ORDER.length()>0){
//			throw new DataAccessException("CMS4J SqlHelper异常：setORDER 方法只能够设置一次，本次设置无效");
//		}

		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			order = removeLawlessStr(order);
		}

		if(this.ORDER.length() > 0){
			this.ORDER = ORDER + " , " + order;
		}else{
			this.ORDER = " ORDER BY " + order;
		}

	}









	/**
	 * 设置DESC
	 * @param desc
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setDESC(String desc) throws DataAccessException{
		setDESC(desc,true);
	}

	/**
	 * 设置DESC
	 * @param desc
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setDESC(String desc,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == desc || desc.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的排序列名称 @ setDESC(String desc,boolean isRemoveLawlessStr)");
			return;
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的排序列名称");
		}

		if(this.DESC.length()>0){
			log.error("CMS4J SqlHelper异常：setDESC 方法只能够设置一次，本次设置无效 @ setDESC(String desc,boolean isRemoveLawlessStr)");
			return;
//			throw new DataAccessException("CMS4J SqlHelper异常：setDESC 方法只能够设置一次，本次设置无效");
		}

		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			desc = removeLawlessStr(desc);
		}

		this.DESC = " " + desc;

	}




	/**
	 * 设置分页
	 * @param position
	 * @param counter
	 * @throws DataAccessException
	 */
	public void setLIMIT(int position,int counter) throws DataAccessException{

//		if(this.LIMIT.length()>0){
//			throw new DataAccessException("CMS4J SqlHelper异常：setLIMIT 方法只能够设置一次，本次设置无效");
//		}
		this.offset = position;

		this.limit = counter;

		//this.LIMIT = " LIMIT " + position + " , " + counter;

	}


	/**
	 * 设置分页(传递页码与页容量)
	 * @param currPageIndex	当前的页码
	 * @param pageSize	每页显示多少
	 * @throws DataAccessException
	 * by pabula 2016-03-17
	 */
	public void setPAGE(int currPageIndex,int pageSize)  throws DataAccessException{
		setLIMIT((currPageIndex - 1) * pageSize, pageSize);
	}


	/**
	 * 设置分页,从请求中获得分页信息
	 * @param request
	 * @throws DataAccessException
     */
	public void setPAGE(HttpServletRequest request) throws  DataAccessException{
		int pageSize = StrUtil.getNotNullIntValue(request.getParameter("pageSize"), 10);
		int pageIndex = StrUtil.getNotNullIntValue(request.getParameter("pageIndex"), 1);

		this.setPAGE(pageIndex,pageSize);
	}


	/**
	 * 移除LIMIT片段
	 * by pabula 2016-03-17
	 */
	public void removePAGE(){
		this.offset = 0;
		this.limit = 0;
	}


	/**
	 * 执行更新
	 * @return
	 * @throws DataAccessException
	 */
	public boolean update(Connection conn,String action) throws DataAccessException {

		if(null == conn){
			throw new DataAccessException("SqlHelper.updata   请提供合法的数据库连接");
		}

		if(null == TABLE || TABLE.trim().equals("") ||
				null == UPDATE_COLUMN || UPDATE_COLUMN.trim().equals("") ||
				null == WHERE || WHERE.trim().equals("")){
			throw new DataAccessException("SqlHelper.updata   参数不完整，" +
					"必须同时俱备 TABLE  UPDATE_COLUMN  WHERE 条件," +
					"当前条件 [TABLE]：" + TABLE + " [UPDATE_COLUMN]: " + UPDATE_COLUMN + " [WHERE]: " + WHERE);
		}
		if(this.isPrepareStmt==false){
			Statement stmt = null;
			// ResultSet rs = null;
			final String sql = getSQL(this.getUpdateSQL());
			try {
				stmt = conn.createStatement();
				return stmt.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DataAccessException("[SqlHelper.Updata 执行失败] " + action,e);
			} finally {
				//ResourceManager.close(rs);
				ResourceManager.close(stmt);
				ResourceManager.close(conn);
			}
		}else{
			PreparedStatement stmt = null;
			final String sql = getPreSQL(this.getUpdateSQL());
			try {
				stmt = conn.prepareStatement(sql);
				wrapPreStmt(stmt,'u');
				return stmt.execute();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DataAccessException("[SqlHelper.Updata 执行失败] " + action,e);
			} finally {
				ResourceManager.close(stmt);
				ResourceManager.close(conn);
			}
		}
	}


	/**
	 * 执行删除
	 * @return
	 * @throws DataAccessException
	 */
	public boolean delete(Connection conn,String action) throws DataAccessException {

		if(null == conn){
			throw new DataAccessException("SqlHelper.delete   请提供合法的数据库连接");
		}

		if(null == TABLE || TABLE.trim().equals("") ||
				null == WHERE || WHERE.trim().equals("")){
			throw new DataAccessException("SqlHelper.updata   参数不完整，" +
					"必须同时俱备 TABLE  WHERE 条件," +
					"当前条件 [TABLE]：" + TABLE + " [WHERE]: " + WHERE);
		}
		if(this.isPrepareStmt==false){
			Statement stmt = null;
			ResultSet rs = null;
			final String sql = getSQL(this.getDeleteSQL());
			try {
				stmt = conn.createStatement();
				return stmt.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DataAccessException("[SqlHelper.Delete 执行失败] " + action,e);
			} finally {
				ResourceManager.close(rs);
				ResourceManager.close(stmt);
				ResourceManager.close(conn);
			}
		}else{
			PreparedStatement stmt = null;
			ResultSet rs = null;
			final String sql = getPreSQL(this.getDeleteSQL());
			try {
				stmt = conn.prepareStatement(sql);
				wrapPreStmt(stmt,'d');
				return stmt.execute();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DataAccessException("[SqlHelper.Delete 执行失败] " + action,e);
			} finally {
				ResourceManager.close(rs);
				ResourceManager.close(stmt);
				ResourceManager.close(conn);
			}
		}
	}










	/**
	 * 执行更新
	 * @return
	 * @throws DataAccessException
	 */
	public boolean insert(Connection conn,String action) throws DataAccessException {

		if(null == conn){
			throw new DataAccessException("SqlHelper.insert   请提供合法的数据库连接");
		}

		if(null == TABLE || TABLE.trim().equals("") ||
				null == INSERT_COLUMN_NAME || INSERT_COLUMN_NAME.trim().equals("") ||
				null == INSERT_COLUMN_VALUE || INSERT_COLUMN_VALUE.trim().equals("")){
			throw new DataAccessException("SqlHelper.updata   参数不完整，" +
					"必须同时俱备 TABLE  INSERT_COLUMN_NAME  INSERT_COLUMN_VALUE 条件," +
					"当前条件 [TABLE]：" + TABLE + " [INSERT_COLUMN_NAME]: " + INSERT_COLUMN_NAME + " [INSERT_COLUMN_VALUE]: " + INSERT_COLUMN_VALUE);
		}
		if(this.isPrepareStmt==false){
			Statement stmt = null;
			//ResultSet rs = null;
			final String sql = getSQL(this.getInsertSQL());
			try {
				stmt = conn.createStatement();
				return stmt.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DataAccessException("[SqlHelper.insert 执行失败] " + action,e);
			} finally {
				// ResourceManager.close(rs);
				ResourceManager.close(stmt);
				ResourceManager.close(conn);
			}
		}else{
			PreparedStatement stmt = null;
			//ResultSet rs = null;
			final String sql = getPreSQL(this.getInsertSQL());
			try {
				stmt = conn.prepareStatement(sql);
				wrapPreStmt(stmt,'i');//包装预编译语句
				return stmt.execute();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DataAccessException("[SqlHelper.insert 执行失败] " + action,e);
			} finally {
				// ResourceManager.close(rs);
				ResourceManager.close(stmt);
				ResourceManager.close(conn);
			}
		}
	}









	/**
	 * 执行查询
	 * @param conn			数据库连接
	 * @param action		行为
	 * @throws DataAccessException
	 */
	public ResultSet select(Connection conn,Statement stmt,String action) throws DataAccessException {

		if(null == conn || null == stmt){
			throw new DataAccessException("SqlHelper.select   请提供合法的数据库连接");
		}


		if(null == TABLE || TABLE.trim().equals("") ||
				null == SELECT_COLUMN || SELECT_COLUMN.trim().equals("")){
			throw new DataAccessException("SqlHelper.updata   参数不完整，" +
					"必须同时俱备 TABLE  SELECT_COLUMN  条件," +
					"当前条件 [TABLE]：" + TABLE + " [SELECT_COLUMN]: " + SELECT_COLUMN);
		}
		if(this.isPrepareStmt==false){
			final String sql = getSQL(this.getSelectSQL());
			try {
				return stmt.executeQuery(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DataAccessException("[SqlHelper.select 执行失败] " + action,e);
			} finally {

				//请注意，这里的连接以及数据库资源都没有被关闭，目的是给调用者利用的空间，故利用此方法的方法，必须关闭这些资源

				//ResourceManager.close(rs);
				//ResourceManager.close(stmt);
				//ResourceManager.close(conn);
			}
		}else{
			final String sql = getPreSQL(this.getSelectSQL());
			try {
				PreparedStatement stm = conn.prepareStatement(sql);
				wrapPreStmt(stm,'s');
				return stm.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DataAccessException("[SqlHelper.select 执行失败] " + action,e);
			}
		}
	}





	/**
	 * 执行查询并取得某列的值（字符型）
	 * @param conn	数据库连接
	 * @param columnName 字段名称
	 * @param action	行为
	 * @return
	 * @throws DataAccessException
	 */
	public String selectAndGetStringValue(Connection conn,String columnName,String action) throws DataAccessException {

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();
			rs = select(conn,stmt,action);

			if(rs.next()){
				return rs.getString(columnName);
			}
		} catch (SQLException e) {
			throw new DataAccessException("[SqlHelper.selectAndGetStringValue 执行失败] " + action,e);
		}finally{
			ResourceManager.close(rs);
			ResourceManager.close(stmt);
			ResourceManager.close(conn);
		}

		return "";
	}




	/**
	 * 执行查询并取得某列的值（数值型）
	 * @param conn			数据库连接
	 * @param columnName	字段名称
	 * @param action		行为
	 * @return
	 * @throws DataAccessException
	 */
	public int selectAndGetIntValue(Connection conn,String columnName,String action) throws DataAccessException {
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();
			rs = select(conn,stmt,action);

			if(rs.next()){
				return rs.getInt(columnName);
			}
		} catch (SQLException e) {
			throw new DataAccessException("[SqlHelper.selectAndGetIntValue 执行失败] " + action,e);
		}finally{
			ResourceManager.close(rs);
			ResourceManager.close(stmt);
			ResourceManager.close(conn);
		}

		return -1;
	}


	/**
	 * 执行查询并取得某列的值（数值型:float）
	 * @param conn
	 * @param columnName
	 * @param action
	 * @return
	 * @throws DataAccessException
	 * @author lw 2007-11-30 下午07:03:38
	 */
	public float selectAndGetFloatValue(Connection conn,String columnName,String action) throws DataAccessException {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = select(conn,stmt,action);

			if(rs.next()){
				return rs.getFloat(columnName);
			}
		} catch (SQLException e) {
			throw new DataAccessException("[SqlHelper.selectAndGetIntValue 执行失败] " + action,e);
		}finally{
			ResourceManager.close(rs);
			ResourceManager.close(stmt);
			ResourceManager.close(conn);
		}

		return -1;
	}




	/**
	 * 合成SQL
	 * @return
	 */
	public String getSQL(String operSql){
		String sql = operSql;

		sql = StrUtil.replaceAll(sql,"$TABLE",TABLE);

		sql = StrUtil.replaceAll(sql,"$WHERE",WHERE);

		sql = StrUtil.replaceAll(sql,"$UPDATE_COLUMN",UPDATE_COLUMN);

		sql = StrUtil.replaceAll(sql,"$SELECT_COLUMN",SELECT_COLUMN);

		sql = StrUtil.replaceAll(sql,"$INSERT_COLUMN_NAME",INSERT_COLUMN_NAME);
		sql = StrUtil.replaceAll(sql,"$INSERT_COLUMN_VALUE",INSERT_COLUMN_VALUE);

		sql = StrUtil.replaceAll(sql,"$GROUP",GROUP);

		sql = StrUtil.replaceAll(sql,"$HAVING",HAVING);

		sql = StrUtil.replaceAll(sql,"$ORDER",ORDER);

		sql = StrUtil.replaceAll(sql,"$DESC",DESC);

//    	sql = StrUtil.replaceAll(sql,"$LIMIT",LIMIT);

		sql = StrUtil.replaceAll(sql,"$LEFT_JOIN",LEFT_JOIN);

		//采用方言进行数据库分页
		sql = this.dialect.getLimitString(sql,offset,limit);

		executeedSql = sql;

		//System.err.println("[SqlHelper SQL: ] " + sql);
		return sql;
	}


	/**
	 * 取预编译SQL
	 * @return
	 */
	public String getPreSQL(String operSql){
		String preSql = operSql;//预编译SQL

		preSql = StrUtil.replaceAll(preSql,"$TABLE",TABLE);

		preSql = StrUtil.replaceAll(preSql,"$WHERE",WHERE_PRE);

		preSql = StrUtil.replaceAll(preSql,"$UPDATE_COLUMN",UPDATE_COLUMN_PRE);

		preSql = StrUtil.replaceAll(preSql,"$SELECT_COLUMN",SELECT_COLUMN);

		preSql = StrUtil.replaceAll(preSql,"$INSERT_COLUMN_NAME",INSERT_COLUMN_NAME);
		preSql = StrUtil.replaceAll(preSql,"$INSERT_COLUMN_VALUE",INSERT_COLUMN_VALUE_PRE);

		preSql = StrUtil.replaceAll(preSql,"$GROUP",GROUP);

		preSql = StrUtil.replaceAll(preSql,"$HAVING",HAVING);

		preSql = StrUtil.replaceAll(preSql,"$ORDER",ORDER);

		preSql = StrUtil.replaceAll(preSql,"$DESC",DESC);

		preSql = StrUtil.replaceAll(preSql,"$LEFT_JOIN",LEFT_JOIN);

		//采用方言进行数据库分页
		preSql = this.dialect.getLimitString(preSql,offset,limit);

		return preSql;
	}


	/**
	 * 去除非法字符
	 * @param str
	 * @return
	 */
	private String removeLawlessStr(String str){

		if(null == str || str.trim().equals("")){
			return str;
		}

		//这里转换为大写，否则 replaceAll区分大小写
		String overStr = str.toUpperCase();

		if(overStr.indexOf("'") >= 0){
			log.error("CMS4J SqlHelper异常：在[" + str + "]不能够使用单引号字符");
			return "CMS4J：SQL中不能够使用'字符";

		}else if(overStr.indexOf("SELECT") >= 0){
			log.error("CMS4J SqlHelper异常：在[" + str + "]不能够使用SELECT字符");
			return "CMS4J：SQL中不能够使用SELECT字符";

		}else if(overStr.indexOf("UPDATE ") >= 0){
			log.error("CMS4J SqlHelper异常：在[" + str + "]不能够使用UPDATE字符");
			return "CMS4J：SQL中不能够使用UPDATE字符";

		}else if(overStr.indexOf("DELETE") >= 0){
			log.error("CMS4J SqlHelper异常：在[" + str + "]不能够使用DELETE字符");
			return "CMS4J：SQL中不能够使用DELETE字符";

		}else if(overStr.indexOf(" UNION ") >= 0){
			log.error("CMS4J SqlHelper异常：在[" + str + "]不能够使用UNION字符");
			return "CMS4J：SQL中不能够使用UNION字符";

		}else if(overStr.indexOf("OUTFILE") >= 0){
			log.error("CMS4J SqlHelper异常：在[" + str + "]不能够使用OUTFILE字符");
			return "CMS4J：SQL中不能够使用OUTFILE字符";

		}else if(overStr.indexOf("/**/") >= 0){
			log.error("CMS4J SqlHelper异常：在[" + str + "]不能够使用/**/字符");
			return "CMS4J：SQL中不能够使用/**/字符";

		}

//    	overStr = StrUtil.replaceAll(overStr,"'","#");
//
//    	overStr = StrUtil.replaceAll(overStr,"SELECT","[SELECT]");
//
//    	overStr = StrUtil.replaceAll(overStr,"UPDATE","[UPDATE]");
//
//    	overStr = StrUtil.replaceAll(overStr,"DELETE","[DELETE]");
//
//    	overStr = StrUtil.replaceAll(overStr,"UNION","[UNION]");
//
//    	overStr = StrUtil.replaceAll(overStr,"OUTFILE","[OUTFILE]");
//
//    	overStr = StrUtil.replaceAll(overStr,"/**/","[^&^]");

		return str;
	}


	/**
	 * 得到已执行的SQL
	 * @return
	 */
	public String getExecuteedSql(){
		return executeedSql;
	}


	public String getDeleteSQL() {
		return dialect.deleteSQL;
	}

//
//	public void setDeleteSQL(String deleteSQL) {
//		this.deleteSQL = deleteSQL;
//	}


	public String getInsertSQL() {
		return dialect.insertSQL;
	}


//	public void setInsertSQL(String insertSQL) {
//		this.insertSQL = insertSQL;
//	}


	public String getSelectSQL() {
		if(this.limit > 0){
			//如果需要分页，则返回分页SQL模板
			return dialect.selectSQLForLimit;
		}else{
			return dialect.selectSQL;
		}

	}


//	public void setSelectSQL(String selectSQL) {
//		this.selectSQL = selectSQL;
//	}


	public String getUpdateSQL() {
		return dialect.updateSQL;
	}




	/**
	 * 设置group by
	 * @param group
	 * @throws DataAccessException
	 */
	public void setGROUP(String group) throws DataAccessException{
		if(null == group || group.trim().equals("")){
			return;
		}

		setGROUP(group,true);
	}


	/**
	 * GROUP子句，可以是表达式
	 * @param group
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setGROUP(String group,boolean isRemoveLawlessStr) throws DataAccessException{

		if(null == group || group.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的 GROUP BY 列名称 @ setGROUP(String group,boolean isRemoveLawlessStr)");
			return;
//    		throw new DataAccessException("CMS4J SqlHelper异常：请提供合法的排序列名称");
		}

//		if(this.ORDER.length()>0){
//			throw new DataAccessException("CMS4J SqlHelper异常：setORDER 方法只能够设置一次，本次设置无效");
//		}

		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			group = removeLawlessStr(group);
		}

		if(this.GROUP.length() > 0){
			this.GROUP = GROUP + " , " + group;
		}else{
			this.GROUP = " GROUP BY " + group;
		}

	}




	/**
	 * 设置having
	 * @param group
	 * @throws DataAccessException
	 */
	public void setHAVING(String having) throws DataAccessException{
		if(null == having || having.trim().equals("")){
			return;
		}

		setHAVING(having,true);
	}


	/**
	 * having子句，可以是表达式
	 * @param group
	 * @param isRemoveLawlessStr
	 * @throws DataAccessException
	 */
	public void setHAVING(String having,boolean isRemoveLawlessStr) throws DataAccessException{

		if(null == having || having.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的 HAVING 条件 @ setHAVING(String having,boolean isRemoveLawlessStr)");
			return;
		}

		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			having = removeLawlessStr(having);
		}

		if(this.HAVING.length() > 0){
			this.HAVING = HAVING + " , " + having;
		}else{
			this.HAVING = " HAVING " + having;
		}

	}

	/**
	 * 设置日期型的Having
	 * @param columnName	字段名称
	 * @param oper	比较符
	 * @param columnValue	值
	 * @param datetimeFormat	日期格式
	 * @param isRemoveLawlessStr 是否移除特殊字符
	 * @throws DataAccessException
	 * @author dekn   2007-11-4 下午05:41:16
	 */
	public void setHavingForDatetime(String columnName,String oper,String columnValue,String datetimeFormat, boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的HAVING列名称 @ setHavingForDatetime(String columnName,String oper,String columnValue,String datetimeFormat, boolean isRemoveLawlessStr)");
			return;
		}

		if(null == columnValue){
			log.info("CMS4J SqlHelper异常：提供的条件值为NULL，已忽略 " + columnName + " CurrWhereSQL: " + this.getCurrWhereSQL());
		}


		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			columnValue = removeLawlessStr(columnValue);
		}


		String sql = "";

		if(StrUtil.isNotNull(datetimeFormat)){
			if(datetimeFormat.trim().equalsIgnoreCase("yyyy-mm-dd hh24:mi:ss")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YYYY + "-" +
								dialect.DATEFORMAT_CODE_MM + "-" +
								dialect.DATEFORMAT_CODE_DD + " " +
								dialect.DATEFORMAT_CODE_HH24 + "-" +
								dialect.DATEFORMAT_CODE_MI + "-" +
								dialect.DATEFORMAT_CODE_SS);
			}else if(datetimeFormat.trim().equalsIgnoreCase("yyyy-mm-dd")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YYYY + "-" +
								dialect.DATEFORMAT_CODE_MM + "-" +
								dialect.DATEFORMAT_CODE_DD);
			}else if(datetimeFormat.trim().equalsIgnoreCase("yy-mm-dd hh24:mi:ss")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YY + "-" +
								dialect.DATEFORMAT_CODE_MM + "-" +
								dialect.DATEFORMAT_CODE_DD + " " +
								dialect.DATEFORMAT_CODE_HH24 + "-" +
								dialect.DATEFORMAT_CODE_MI + "-" +
								dialect.DATEFORMAT_CODE_SS);
			}else if(datetimeFormat.trim().equalsIgnoreCase("yyyy-mm")){
				columnValue = dialect.getTimeFragment(columnValue,
						dialect.DATEFORMAT_CODE_YYYY + "-" +
								dialect.DATEFORMAT_CODE_MM);
			}

		}

		//根据不同的类型,设置column的SQL片段
		sql = columnName + oper + columnValue;



		//判断是不是要进行拼接
		if(this.HAVING.trim().length() > 0){
			this.HAVING = this.HAVING + " , " + sql;
		}else{
			this.HAVING =  " HAVING " + sql;
		}
	}


	/**
	 * 设置HAVING
	 * @param columnName 字段名称
	 * @param oper 关系运算符
	 * @param columnValue 字段值
	 * @param isRemoveLawlessStr 是否移除特殊字符
	 * @throws DataAccessException
	 * @author dekn   2007-11-4 下午06:11:59
	 */
	public void setHavingForInt(String columnName,String oper,int columnValue, boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的条件列名称 @ setHavingForInt(String columnName,String oper,int columnValue, boolean isRemoveLawlessStr)");
			return;
		}

		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = columnName + oper + columnValue;

		//判断是不是要进行拼接
		if(this.HAVING.trim().length() > 0){
			this.HAVING = this.HAVING + " , " + sql;
		}else{
			this.HAVING = " HAVING " + sql;
		}
	}

	/**
	 * 设置HAVING
	 * @param columnName 字段名称
	 * @param oper 关系运算符
	 * @param columnValue 字段值
	 * @param isRemoveLawlessStr 是否移除特殊字符
	 * @throws DataAccessException
	 * @author dekn   2007-11-4 下午06:11:23
	 */
	public void setHavingForString(String columnName,String oper,String columnValue, boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的条件列名称 @ setHavingForString(String columnName,String oper,String columnValue, boolean isRemoveLawlessStr)");
			return;
		}

		if(null == columnValue){
			log.info("CMS4J SqlHelper异常：提供的条件值为NULL，已忽略 " + columnName + " CurrWhereSQL: " + this.getCurrWhereSQL());
		}


		//去除特殊字符,如'号
		if(isRemoveLawlessStr){
			columnValue = removeLawlessStr(columnValue);
		}

		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = columnName + oper + "'" + columnValue + "'";

		//判断是不是要进行拼接
		if(this.HAVING.trim().length() > 0){
			this.HAVING = this.HAVING + " , " + sql;
		}else{
			this.HAVING =  " HAVING " + sql;
		}
	}

	/***
	 * 包装预编译SQL
	 * @param pStmt 准备语句
	 * @param fieldValue 字段值的集合
	 * @author lala 2008-12-8 下午04:16:02
	 */
	public void wrapPreStmt(PreparedStatement pStmt,char type){
		switch(type){
			case 's':{

			}
			case 'd':{
				try {
					Iterator it = whereValue.iterator();
					int index = 1;
					while(it.hasNext()){
						Object value = it.next();
						String valueStr = String.valueOf(value);
						int length = valueStr.indexOf("@date_type_prefix@_");
						if(length==0){
							pStmt.setTimestamp(index, DateUtil.getNotNullTimestampValue(valueStr.substring(19),"yyyy-MM-dd HH:mm:ss"));
						}else{
							pStmt.setObject(index, value);
						}
						index++;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			}
			case 'u':{
				try {
					Iterator it = updateColumnValue.iterator();
					int index = 1;
					while(it.hasNext()){
						Object value = it.next();
						String valueStr = String.valueOf(value);
						int length = valueStr.indexOf("@date_type_prefix@_");
						if(length==0){
							pStmt.setTimestamp(index, DateUtil.getNotNullTimestampValue(valueStr.substring(19),"yyyy-MM-dd HH:mm:ss"));
						}else{
							pStmt.setObject(index, value);
						}
						index++;
					}

					it = whereValue.iterator();
					while(it.hasNext()){
						Object value = it.next();
						String valueStr = String.valueOf(value);
						int length = valueStr.indexOf("@date_type_prefix@_");
						if(length==0){
							pStmt.setTimestamp(index, DateUtil.getNotNullTimestampValue(valueStr.substring(19),"yyyy-MM-dd HH:mm:ss"));
						}else{
							pStmt.setObject(index, value);
						}
						index++;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			}

			case 'i':{
				try {
					Iterator it = insertColumnValue.iterator();
					int index = 1;
					while(it.hasNext()){
						Object value = it.next();
						String valueStr = String.valueOf(value);
						int length = valueStr.indexOf("@date_type_prefix@_");
						if(length==0){
							pStmt.setTimestamp(index, DateUtil.getNotNullTimestampValue(valueStr.substring(19),"yyyy-MM-dd HH:mm:ss"));
						}else{
							pStmt.setObject(index, value);
						}
						index++;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			}
			default:{

			}
		}
	}

	/**
	 * 设置要更新的字段
	 * @author qlf
	 * @param void
	 * @datetime 2011-8-8 下午04:36:50
	 */
	public void setColumnForDecimal(String columnName,BigDecimal columnValue) throws DataAccessException{
		setColumnForDecimal(columnName, columnValue,true);
	}

	/**
	 * 设置要更新的字段
	 * @author qlf
	 * @param void
	 * @datetime 2011-8-8 下午04:36:50
	 */
	public void setColumnForDecimal(String columnName,BigDecimal columnValue,boolean isRemoveLawlessStr) throws DataAccessException{
		if(null == columnName || columnName.trim().equals("")){
			log.error("CMS4J SqlHelper异常：请提供合法的列名称 @ setColumnForInt(String columnName,int columnValue,boolean isRemoveLawlessStr)");
			return;
		}

		String sql = "";

		//根据不同的类型,设置column的SQL片段
		sql = columnName + "=" + columnValue;
		//判断是不是要进行拼接
		if(this.UPDATE_COLUMN.trim().length() > 0){
			this.UPDATE_COLUMN = this.UPDATE_COLUMN + "," + sql;
		}else{
			this.UPDATE_COLUMN = sql;
		}

		if(this.isPrepareStmt){
			String sqlPre = columnName + "= ? ";

			//判断是不是要进行拼接
			if(this.UPDATE_COLUMN_PRE.trim().length() > 0){
				this.UPDATE_COLUMN_PRE = this.UPDATE_COLUMN_PRE + "," + sqlPre;
			}else{
				this.UPDATE_COLUMN_PRE = sqlPre;
			}

			updateColumnValue.add(String.valueOf(columnValue));
		}
	}
}
