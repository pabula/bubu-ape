package com.pabula.dao;

import com.alibaba.fastjson.JSONObject;
import com.pabula.common.util.ResourceManager;
import com.pabula.fw.exception.DataAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 通用的数据库访问
 * Created by pabula on 16/7/4.
 */
public class CommonDAO {


    /**
     * 根据提供的SQL模板，返回数据源部分
     * @param sqlStr    jiaorder:INSERT INTO unit ({$ALL_COLUMN}) values ({$DATA})
     * @return
     */
    private String getDBSource(String sqlStr){
        String dbSource = sqlStr.substring(0,sqlStr.indexOf(":"));
        return dbSource;
    }

    /**
     * 根据提供的sql模板，返回SQL语句部分
     * @param sqlStr    jiaorder:INSERT INTO unit ({$ALL_COLUMN}) values ({$DATA})
     * @return
     */
    private String getSql(String sqlStr){
        String sql = sqlStr.substring(sqlStr.indexOf(":") + 1);
        return sql;
    }

    /**
     * 执行sql语句
     * @param sqlStr    jiaorder:INSERT INTO unit ({$ALL_COLUMN}) values ({$DATA})
     * @return
     * @throws DataAccessException
     */
    public boolean execute(String sqlStr) throws DataAccessException {
        String dbSource = getDBSource(sqlStr);
        String sql = getSql(sqlStr);

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ResourceManager.getConnection(); //TODO 多数据源的处理，需要在这里处理上
            stmt = conn.createStatement();
            return stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("[CommonDAO execute 执行失败] " + sqlStr,e);
        } finally {
            //ResourceManager.close(rs);
            ResourceManager.close(stmt);
            ResourceManager.close(conn);
        }
    }

    /**
     * 执行update
     * @param sqlStr    jiaorder:INSERT INTO unit ({$ALL_COLUMN}) values ({$DATA})
     * @return
     * @throws DataAccessException
     */
    public boolean update(String sqlStr) throws DataAccessException {
        return this.execute(sqlStr);
    }

    /**
     * 删除
     * @param sqlStr
     * @return
     * @throws DataAccessException
     */
    public boolean delete(String sqlStr) throws DataAccessException {
        return this.execute(sqlStr);
    }

    /**
     * 插入
     * @param sqlStr
     * @return
     * @throws DataAccessException
     */
    public boolean insert(String sqlStr) throws DataAccessException {
        return this.execute(sqlStr);
    }


    /**
     * select 后，将结果集包装成一个JSONObject式的LIST返回
     * @param sqlStr
     * @return
     * @throws DataAccessException
     */
    public List<JSONObject> select(String sqlStr) throws DataAccessException {
        List<JSONObject> resultList = new ArrayList<>();

        String dbSource = getDBSource(sqlStr);  //TODO  多数据源的处理
        String sql = getSql(sqlStr);

        Connection conn=null;
        Statement stmt=null;
        ResultSet rs=null;
        try {
            conn=ResourceManager.getConnection();
            stmt=conn.createStatement();
            rs=stmt.executeQuery(sql);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            //metaData.getTableName(i)   TODO 不确定这句能不能得到对齐列的表名称，要测试，如果能得到，并且支持多表重名的情况下得到，那就不用在写SQL时，写别名了

            while(rs.next()){
                JSONObject jsonObj = new JSONObject();

                // 遍历每一列
                for (int i = 1; i <= columnCount; i++) {
                    String columnName =metaData.getColumnLabel(i);
                    String value = rs.getString(columnName);
                    jsonObj.put(columnName, value);
                }
                resultList.add(jsonObj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("[CommonDAO select 执行失败]", e);
        } finally {
            ResourceManager.close(rs);
            ResourceManager.close(stmt);
            ResourceManager.close(conn);
        }
        return resultList;
    }

    /**
     * 查询某一条记录，并将记录返回 JSONObject
     * @param sqlStr
     * @return 记录的JSONObject
     * @throws DataAccessException
     */
    public JSONObject getByID(String sqlStr) throws DataAccessException {
        List<JSONObject> list = this.select(sqlStr);
        if(null != list && list.size() > 0){
            return list.get(0);
        }

        return new JSONObject();
    }

    /**
     * 查询结果集的数量
     * @param sqlStr 默认以count做为关键字，即 count(*) as count
     * @return count
     * @throws DataAccessException
     */
    public int count(String sqlStr) throws DataAccessException {
        int count = 0;

        String dbSource = getDBSource(sqlStr);  //TODO  多数据源的处理
        String sql = getSql(sqlStr);

        Connection conn=null;
        Statement stmt=null;
        ResultSet rs=null;
        try {
            conn=ResourceManager.getConnection();
            stmt=conn.createStatement();
            rs=stmt.executeQuery(sql);

            if(rs.next()){
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("[CommonDAO select 执行失败]", e);
        } finally {
            ResourceManager.close(rs);
            ResourceManager.close(stmt);
            ResourceManager.close(conn);
        }

        return count;
    }




}
