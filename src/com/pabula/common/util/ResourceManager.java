/*
 * Created on 2004-10-12
 */
package com.pabula.common.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.logicalcobwebs.proxool.ProxoolDriver;

import java.sql.*;
import java.util.Properties;

/**
 * @author Dekn
 *
 *         数据库连接管理
 */
public class ResourceManager {


  public static synchronized Connection getConnection() throws SQLException {
    Connection conn = null;
    try {
      //use 'proxool 0.8.3'
      Properties info = new Properties();
      info.setProperty("proxool.maximum-connection-count", "30");
      info.setProperty("proxool.prototype-count", "4");
      info.setProperty("proxool.house-keeping-test-sql", "select CURRENT_DATE");
      info.setProperty("proxool.statistics", "10s,1m,1d");
      //info.setProperty("proxool.maximum-active-time","172800000");
      info.setProperty("user", "root");//数据库用户名
      //            info.setProperty("user", "huanhuanla");
      info.setProperty("password", "root");//密码
      //            info.setProperty("password", "1q2w3e4r0p9o8i7uhuanhuan");
      String alias = "cms4jCP";
      String driverClass = "com.mysql.jdbc.Driver";
      //数据库连接地址
      String driverUrl = "jdbc:mysql://192.168.2.200:3306/jiaorder?useUnicode=true&characterEncoding=utf8";
      //String driverUrl = "jdbc:mysql://huanhuanla.mysql.rds.aliyuncs.com:3306/jiaorder?useUnicode=true&characterEncoding=utf8";
      String url = "proxool." + alias + ":" + driverClass + ":" + driverUrl;
      ProxoolDriver proxoolDriver = new ProxoolDriver();
      conn = proxoolDriver.connect(url, info);
      //conn = DriverManager.getConnection(url, info);

      //PropertyConfigurator.configure(appProperties);
      //conn = DriverManager.getConnection("proxool.JSPBook");

    } catch (SQLException ex) {
      throw ex;
    } catch (Exception e) {
      System.out.println(
          "数据库连接失败，有以下可能：数据库服务未启动|数据连接URL错误|数据库连接用户名或密码错误");
      throw new SQLException(
          "数据库连接失败，有以下可能：数据库服务未启动|数据连接URL错误|数据库连接用户名或密码错误");
    }
    return conn;
  }

  public static void close(Connection conn) {
    try {
      if (conn != null) {
        conn.close();
        conn = null;
      }
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    }
  }

  public static void close(PreparedStatement stmt) {
    try {
      if (stmt != null) {
        stmt.close();
        stmt = null;
      }
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    }
  }

  public static void close(Statement stmt) {
    try {
      if (stmt != null) {
        stmt.close();
        stmt = null;
      }
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    }
  }

  public static void close(ResultSet rs) {
    try {
      if (rs != null) {
        rs.close();
        rs = null;
      }
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    }
  }
}