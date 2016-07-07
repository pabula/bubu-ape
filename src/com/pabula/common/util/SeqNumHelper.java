/*
 * 创建于 2005-2-4 15:06:31
 * JCMS
 */
package com.pabula.common.util;

import com.pabula.db.SqlHelper;
import com.pabula.fw.exception.DataAccessException;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author Dekn 序列号辅助类，负责生成记录的序列号
 */
public class SeqNumHelper {

	static Logger log = Logger.getLogger(SeqNumHelper.class);

	public static final int NO_SERVICE_ID = 1;

	/**
	 * 取得最新可用的序列号
	 * 
	 * @param seqType
	 * @return
	 * @throws DataAccessException
	 */
	public synchronized static int getNewSeqNum(String seqType)throws DataAccessException {
		return getNewSeqNum(NO_SERVICE_ID,seqType);
	}
	/**
	 * 取得最新可用的序列号
	 *
	 * @param seqType
	 * @return
	 * @throws DataAccessException
	 */
	public synchronized static int getNewSeqNum(int serviceId, String seqType)throws DataAccessException {
		return getNewSeqNum(serviceId, seqType, 1);
	}

	/**
	 * 双重序列号生成
	 * @param dbSource
	 * @param seqType1
	 * @param seqType2
	 * @return
	 * @throws DataAccessException
	 */
	public synchronized static int getNewSeqNum(String dbSource,String seqType1,String seqType2)throws DataAccessException {
		return 0;	//TODO 实现多数据源的序列号生成
	}

	public static String getSeqTableName() {
		return "SYS_SEQUENCE";
	}

	/**
	 * 取得最新可用的序列号
	 * 
	 * @param seqType
	 * @return
	 * @throws DataAccessException
	 */
	private static int getNewSeqNum(int serviceId, String seqType, int count)throws DataAccessException {
		// 转换为大写
		seqType = seqType.toUpperCase();

		// 是否存在某序列号
		if (!isExistSeqNum(serviceId, seqType)) {
			// 不存在则创建默认的
			createDefaultSeqNum(serviceId, seqType, 1001 + count);
			return 1000;
		}

		int seq = 0;

		int i = 0;

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			final String sql = "select SEQ_NUM from " + getSeqTableName() + " where SERVICE_ID = ? and  SEQ_TYPE = ?";

			conn = ResourceManager.getConnection();

			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, serviceId);
			stmt.setString(2, seqType);

			rs = stmt.executeQuery();

			// 如果成功取出最新序列号
			if (rs.next()) {
				i = -1;
				seq = rs.getInt("SEQ_NUM");

			}
		} catch (SQLException e) {
			log.fatal(e);
			e.printStackTrace();
			throw new DataAccessException("DAO　Layou 取得最新可用的序列号 : ", e);
		} finally {
			ResourceManager.close(rs);
			ResourceManager.close(stmt);
			ResourceManager.close(conn);
		}
		// 如果成功取出最新序列号
		if (i == -1) {
			updateSeqNum(serviceId, seq + count, seqType);// 现有序列号+count
		}

		return seq;
	}

	/**
	 * 更新序列号表中的序列号
	 * 
	 * @param nextSeqNum
	 *            下一个可以使用的序列号
	 * @param seqType
	 *            序列号类型
	 * @return
	 * @throws DataAccessException
	 */
	private static boolean updateSeqNum(int serviceId, int nextSeqNum, String seqType)throws DataAccessException {
		boolean isOK = false;

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			final String sql = "update " + getSeqTableName() + " set SEQ_NUM = ? where SERVICE_ID = ? AND SEQ_TYPE = ?";
			conn = ResourceManager.getConnection();

			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, nextSeqNum);
			stmt.setInt(2, serviceId);
			stmt.setString(3, seqType.toUpperCase());

			if (stmt.executeUpdate() > 0) {
				isOK = true;
			}
		} catch (SQLException e) {
			log.fatal(e);
			e.printStackTrace();
			throw new DataAccessException("DAO　Layou 更新序列号表中的序列号 : ", e);
		} finally {
			ResourceManager.close(rs);
			ResourceManager.close(stmt);
			ResourceManager.close(conn);
		}

		return isOK;
	}

	/**
	 * 是否存在某序列号
	 * 
	 * @param seqType
	 * @return
	 * @throws DataAccessException
	 */
	private static boolean isExistSeqNum(int serviceId, String seqType)throws DataAccessException {
		try {
			SqlHelper sqlHelper = new SqlHelper();

			// SELECT COLUMN 字段
			sqlHelper.setSelectColumn("COUNT(*) AS COUNT");

			// FROM TABLE 表格
			sqlHelper.setTable(getSeqTableName());

			//过滤同一serviceID
			sqlHelper.setWhereForInt("SERVICE_ID", "=", serviceId);

			// WHERE 条件
			sqlHelper.setWhereForString("SEQ_TYPE", " = ", seqType.toUpperCase());

			// 执行
			int count = sqlHelper.selectAndGetIntValue(ResourceManager.getConnection(), "COUNT", "是否存在某序列号");

			if (count > 0) {
				return true;
			}

			return false;

		} catch (DataAccessException e) {
			throw e;
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	/**
	 * 创建默认的序列号记录
	 * 
	 * @param sqeType
	 * @param defSeqNum
	 * @throws DataAccessException
	 */
	private static void createDefaultSeqNum(int serviceId, String sqeType, int defSeqNum)throws DataAccessException {
		try {
			SqlHelper sh = new SqlHelper();
			// FROM TABLE 表格
			sh.setTable(getSeqTableName());

			// INSERT COLUMN NAME AND VALUE 条件
			sh.setInsertForString("SEQ_TYPE", sqeType.toUpperCase());
			sh.setInsertForInt("SEQ_NUM", defSeqNum);
			sh.setInsertForInt("SERVICE_ID", serviceId);

			// INSERT
			sh.insert(ResourceManager.getConnection(), "创建默认的序列号记录");

		} catch (DataAccessException e) {
			throw e;
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

}
