package com.bubu.templet.bean;

import com.pabula.db.SqlHelper;

/**
 * 在DAO层,加上BEAN,目标是可以在这里做分表\缓存等与数据库有关的中间层
 * Created by Pabula on 16/6/26 20:08.
 */
public class TableBean {

    public void add(String dataJson){

    }

    //todo 像这里,方法的参数是不定的,又怎么实现动态的? 所以,也不能用这种类方法的方式,估计还要JSON报文的定义
    public void modify(String serviceID,String id,String dataJson){

    }

    public void modify(String dataJson){
        //TODO 方法接收所有数据包,然后在这里根据业务需求,结合数据包,对数据库开始操纵

        //SqlHelper sqlHelper = new SqlHelper("");
        // sqlHelper.set.....
        // getSQL();

        // dao.execute(sql);
    }

    public void del(){

    }

    public void update(){

    }

    public void get(){

    }

    public void query(){

    }

}

