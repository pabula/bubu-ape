/*
 * ������ 2004-11-11 16:13:26
 * JCMS
 */
package com.pabula.fw.exception;

import javax.servlet.ServletException;
import java.util.Collection;

/**
 * @author Dekn
 * �����쳣
 * JCMS ( Content Manager System for java )
 */
public class RuleException extends ServletException {
    
    private Collection errColl;
    
    public RuleException(Throwable cause)
    {
        super(cause);
    }

    public RuleException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public RuleException(String ex)
    {
        super(ex);
    }

    public RuleException()
    {
    }
    
    
    
    /**
     * @return Returns the errColl.
     */
    public Collection getErrColl() {
        return errColl;
    }
    
    /**
     * @param errColl The errColl to set.
     */
    public void setErrColl(Collection errColl) {
        this.errColl = errColl;
    }
}
