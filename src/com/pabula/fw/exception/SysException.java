package com.pabula.fw.exception;


import javax.servlet.ServletException;

/**
 * ϵͳ�쳣
 * @author dekn     May 25, 2006 6:05:44 PM
 */
public class SysException extends ServletException {
    
//    private Collection errColl;
    
    public SysException(Throwable cause)
    {
        super(cause);
    }

    public SysException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SysException(String ex)
    {
        super(ex);
    }

    public SysException()
    {
    }
    
//    
//    
//    /**
//     * @return Returns the errColl.
//     */
//    public Collection getErrColl() {
//        return errColl;
//    }
//    
//    /**
//     * @param errColl The errColl to set.
//     */
//    public void setErrColl(Collection errColl) {
//        this.errColl = errColl;
//    }
}
