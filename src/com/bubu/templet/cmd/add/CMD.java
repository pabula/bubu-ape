package com.bubu.templet.cmd.add;

import com.bubu.templet.vo.Table;
import com.pabula.cmd.ValidateHelper;
import com.pabula.common.util.StrUtil;

/**
 * Created by pabula on 16/6/19.
 */
public class CMD {

    String dataJson;    //数据的JSON报文
    String ruleJson;    //配置文件中规则的JSON


    /**
     * 装载数据
     */
    private void loadData(){

        //根据 request 中的 param \ 此CMD绑定的TABLE 两项指标,将 PARAM 填充至 TABLE vo 中


        //方案1: 还用传统的VO方式;不方便增加数据项,比如一个不需要入库的程序(导入),还为专门为这个功能建一个VO类
//        Table table = new Table();
//        table.setTable_count(1);
//        table.setTable_id("1000");
//        table.setTable_name("iphone 6s plus");


        //方案2,直接是操作一个TABLE的JSON,进行JSON数据的填充. 好处是,没有了类的处理,并且后期处理,也不用做类的反射;增加数据项,随时可以加

        this.dataJson = "{json}";

    }



    /**
     * 做validate
     * @return
     */
    private String validate(){

        String errMsg = "";

        errMsg = ValidateHelper.validate(this.dataJson,this.ruleJson);

        // 手工补写一些业务规则(弥补上面自动化做不到的)

        return errMsg;
    }


    /**
     * 执行
     */
    private String execute(){
        // 修正一次入库前的VO,可以是放入默认值\增加一些值\根据业务逻辑,改变值等

        //TODO 像排序编号这样的计算逻辑应该写在哪里呢
        if("是否支持表数据排序" == "开启"){
            //TABLE.ORDER_NUM = getOrderNum("table");   //TODO 如果还要传 service_id 这样的不定参数,怎么办?
        }


        String returnData = "";




        // serviceId = UserHelper.getServiceID(request);

        // 调用业务
        // GuestbookAgentBean.newInstance().addGuestbook(vo);

        // 日志记录
        // LogUtil.operLog(LogType.OTHER, "新增建议反馈",ID,request);

        /*****************************************************
         * 返回: 数据包 + 返回页面
         *****************************************************/
        // return JsonResultUtil.ok();
        // JsonResultUtil.instance().addData(page).json();



        return "";
    }


    public void main(){

        /*****************************************************
         * 将REQUEST对象,填充至TABLE VO对象中
         *****************************************************/
        loadData();


        /*****************************************************
         * 做 Validate,即值的合法性判断
         *****************************************************/

        // 根据 json 定义文件中定义的validate规则,将数据包交给一个处理器,动态的判定
        if(StrUtil.isNotNull(validate())){
            //将错误信息,返回给前台
        }


        /*****************************************************
         * 调用业务
         *****************************************************/
        execute();


    }


}
