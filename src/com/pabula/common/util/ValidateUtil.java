/*
 * ������ 2004-11-7 22:03:12
 * JCMS
 */
package com.pabula.common.util;
import java.util.ArrayList;

/**
 * @author Dekn
 * 
 * ���ݼ����࣬���ҿ��Թ��������ϢLIST
 * 
 * JCMS ( Content Manager System for java )
 */
public class ValidateUtil {
    
    ArrayList errorList;
    
    public ValidateUtil(){
        errorList = new ArrayList();
        this.clear();
    }
    
    /**
     * �Ƿ�ΪNULL
     * @param data��Ҫ��������
     * @return
     */
    public boolean isNull(String data){
        boolean isNull = false;
        if(data == null){
            isNull = true;
        }
        return isNull;
    }
    
    /**
     * �Ƿ�Ϊ��
     * @param data
     * @return
     */
    public boolean isEmpty(String data){
        boolean isNull = false;
        if(data.equals("")){
            isNull = true;
        }
        return isNull;
    }
    
    /**
     * �Ƿ�Ϊ�ջ�NULL
     * @param data
     * @return
     */
    public boolean isEmptyOrNull(String data){
        boolean isNull = false;
        if(data == null || data.trim().equals("")){
            isNull = true;
        }
        return isNull;
    }
    
    
    /**
     * �����Ƿ�ΪNULL�������ṩ������Ϣ
     * @param data��Ҫ��������
     * @param errMsg�����Ϊ��ʱ����ʾ�Ĵ�����Ϣ
     * @return
     */
    public String validateIsNull(String data,String errMsg){
        boolean isNull = this.isNull(data);
        if(isNull){
            if (errMsg != null || !errMsg.equals("")){
                this.errorList.add(errMsg);
            }
        }
        return data; 
    }
    
    /**
     * �����Ƿ�Ϊ�գ������ṩ������Ϣ
     * @param data��Ҫ��������
     * @param errMsg�����Ϊ��ʱ����ʾ�Ĵ�����Ϣ
     * @return
     */    
    public String validateIsEmpty(String data,String errMsg){
        boolean isEmpty = this.isEmpty(data);
        if(isEmpty){
            if (errMsg != null || !errMsg.equals("")){
                this.errorList.add(errMsg);
            }
        }
        return data; 
    }
    
    
    /**
     * �����Ƿ�ΪNULL��գ������ṩ������Ϣ
     * @param data��Ҫ��������
     * @param errMsg�����Ϊ��ʱ����ʾ�Ĵ�����Ϣ
     * @return
     */   
    public String validateIsEmptyOrNull(String data,String errMsg){
        boolean isEmptyOrNull = this.isEmptyOrNull(data);
        if(isEmptyOrNull){
            if (errMsg != null || !errMsg.equals("")){
                this.errorList.add(errMsg);
            }
        }
        return data; 
    }
    
    /**
     * ���������ǲ�������
     * @param data Ҫ���������
     * @param errMsg ����������֣��ṩ�Ĵ�����Ϣ
     * @return
     */
    public int validateIsNumber(String data,String errMsg){
        int number = 0;
        
        try {
            number = Integer.parseInt(data); 	
            //if (number < 0){
            //    this.errorList.add(errMsg);
           // }
        }catch (NumberFormatException e){
            this.errorList.add(errMsg);
        }

        return number;
    }
    
    /**
     * ���������ǲ��Ǵ���ָ������
     * @param data Ҫ���������
     * @param errMsg ����������֣��ṩ�Ĵ�����Ϣ
     * @return
     */
    public int validateMore(String data,int more,String errMsg){
        int number = 0;
        
        try {
            number = Integer.parseInt(data); 	
            if (number > more){
                this.errorList.add(errMsg);
            }
        }catch (NumberFormatException e){            
        }

        return number;
    }
    
    /**
     * ���������ǲ��Ǵ���ָ������
     * @param data Ҫ���������
     * @param errMsg ����������֣��ṩ�Ĵ�����Ϣ
     * @return
     */
    public int validateLess(String data,int less,String errMsg){
        int number = 0;
        
        try {
            number = Integer.parseInt(data); 	
            if (number < less){
                this.errorList.add(errMsg);
            }
        }catch (NumberFormatException e){            
        }

        return number;
    }
    
    /**
     * �Ƿ��д������
     * @return
     */
    public boolean hasError(){
        if (errorList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * �����list�����һ��������Ϣ
     * @param errMsg ����ӵĴ�����Ϣ
     * @return
     */
    public boolean addError(String errMsg){
        return this.errorList.add(errMsg);
    }
    
    /**
     * ȡ�ò��������д���
     * @return
     */
    public ArrayList getErrors(){
        return this.errorList;
    }
    
    public void clear(){
        this.errorList.clear();
    }
    
    
}
