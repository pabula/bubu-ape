/*
 * ������ 2004-10-18 21:06:01
 * JCMS
 */
package com.pabula.fw.exception;

import javax.servlet.ServletException;

/**
 * @author Dekn
 *
 * JCMS ( Content Manager System for java )
 */
public class DataAccessException extends ServletException {

    /**
     * @param message
     */
    public DataAccessException(String message) {
            super(message);
            // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public DataAccessException(Throwable cause) {
            super(cause);
            // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public DataAccessException(String message, Throwable cause) {
            super(message, cause);
            // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see cn.com.netkiss.exception.UniApplicationException#getRealm()
     */
    public String getRealm() {
            return "数据访问异常";
    }

    /* (non-Javadoc)
     * @see cn.com.netkiss.exception.UniApplicationException#getDefine()
     */
    public String getDefine() {
            // TODO Auto-generated method stub
            return null;
    }

}
