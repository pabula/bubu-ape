package com.pabula.db.Dialect.sql;

import com.pabula.common.util.DateUtil;


/**
 * db2������
 * @author chihiro
 * 2008-10-9
 */
public class DB2Dialect extends Dialect{
	
	public DB2Dialect(){
		super();
		
		//�����У����ڸ�ʽ���룬������oracle�ı��뷽ʽ
		DATEFORMAT_CODE_YYYY = "yyyy";
		DATEFORMAT_CODE_YY 	= "yy";
		DATEFORMAT_CODE_MM 	= "mm";
		DATEFORMAT_CODE_DD 	= "dd";
		DATEFORMAT_CODE_HH 	= "HH";
		DATEFORMAT_CODE_HH24 = "hh24";
		DATEFORMAT_CODE_MI 	= "mi";
		DATEFORMAT_CODE_SS 	= "ss";
		
		//��ǰ���ڷ���
		FUNCTION_NOW = "current timestamp";		//db2ʱ�������
		
		//db2�����ڸ�ʽ��
		FROMAT = "yyyy-mm-dd hh24:mi:ss";
		
		//�ؼ��ֶ���
		SQL_KEY_ALIAS = " as ";		
	}
	
	
	/**
	 * ȡ�÷�ҳ���SQL
	 * @param sql
	 * @param hasOffset	�Ƿ�ΪȡǰN��
	 * @param offset	��ʼ
	 * @param limit	����
	 * @return
	 */
	public String getLimitString(String sql, boolean hasOffset,int offset,int limit) {
		if(offset <= 0 && limit <= 0){
			return sql;
		}
		
		sql = sql.trim();
		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 );
		
		pagingSelect.append("select * from (select row_number()over() as row_num,tmp.* from (");
		
		pagingSelect.append(sql);
		if (hasOffset) {
			pagingSelect.append(" ) tmp ) temp where temp.row_num <= " + (limit + offset) + " and temp.row_num > " + offset);
		}else {
			pagingSelect.append(" ) tmp ) temp where temp.row_num <= " + limit);
		}

		
		return pagingSelect.toString();
	}
	
	
	/**
	 * ȡ��һ��ʱ���Ƭ��
	 * @param time	�� 2006-11-18
	 * @param foramt	�� yyyy-mm-dd
	 * @return �����oracle���ԣ��򷵻�  to_date('2006-11-18','yyyy-mm-dd');
	 */
	public String getTimeFragment(String time,String format){
		return "'" + time + "'";
	}
	
	/**
	 * ��ȡ���ڱȽϱ��ʽ
	 * @param columnName	�Ƚϵ��ֶ�
	 * @param compareOper	�Ƚ������
	 * @param compareValue	�Ƚ�ֵ
	 * @param dateFormat	���ڸ�ʽ
	 * @return
	 */
	public String getDateCompareString(String columnName,String compareOper,String compareValue,String dateFormat){
		return new StringBuffer(columnName.length() + 50).
					append(" substr(cast(").
					append(columnName).
					append(" as varchar(30)),1,").
					append(dateFormat.length()).
					append(")").
					append(compareOper).
					append("'").
					append(compareValue).
					append("'").
					toString();
	}
	
	
	public String functionToInt(String columnName){
		return "cast(" + columnName + " as integer)";
	}
	
	/**
	 * תΪΪ�ַ���
	 */
	public String functionTochar(String column){
		return "char(" + column + ")";
	}
	
	/**
	 * ��ȡDB2���ݿ��ʱ�������
	 */
	public String getFUNCTION_NOW(){
		String date = DateUtil.getCurrentDay("yyyy-MM-dd HH-mm-ss");
		return getTimeFragment(date,FROMAT);
	}
	
	/**
	 * ���ȡ��¼
	 * @author skayee
	 * @return 
	 */
	public String functionRandomByOrder(){
		return "rand()";
	}
}
