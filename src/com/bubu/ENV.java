package com.bubu;

/**
 * Created by sunsai on 2016/2/19.
 */
public class ENV {

  //是否为测试模式
  public static boolean IS_TEST = true;//
  //ssss
  //业务的主包名称，所有的模块都在这个大包下
  final public static String PACKAGE_NAME = "com.jiaorder";
  final public static String VO_PACKAGE_NAME = "com.jiaorder";

  //体验帐号
  final public static String DEMO_USER_AGENT = "13966667756-2"; //pc订货端 体验用户
  final public static String DEMO_USER_ADMIN = "13966667756"; //PC管理端 体验用户

  //cookie中存放的用户校验码加密KEYVO_
  public static final String MEMBER_COOKIE_PWD_CHECK = "hhdaojiapabulaveryok";

  //cookie中存放的购物车的cookie名称
  public static final String CART_COOKIE_NAME = "PRDCART";

  //短信相关参数（http://www.yuntongxun.com)
  public static final String SMS_TELPLET_ID = "72581";    //验证码模板ID
  public static final String SMS_TELPLET_ID_NEWPWD = "72824";    //验证码模板ID
  public static final String SMS_TELPLET_ID_RESETPWD = "72680";    //验证码模板ID
  public static final String SMS_APP_ID = "8a48b55150a898370150a9eb2b710512"; //应用ID
  public static final String SMS_ACCOUNT_SID = "aaf98f894e2360b4014e32ba8eba0d30";    //ACCOUNT_SID
  public static final String SMS_AUTH_TOKEN = "40b12dfec56c4f1fa66eaa3ff0903a7c"; //AUTH_TOKEN
  public static final String SMS_SERVER = "app.cloopen.com"; //AUTH_TOKEN
  public static final String SMS_SERVER_PORT = "8883"; //AUTH_TOKEN

  //URL domain
  public static String URL_DOMAIN = "http://admin.jiaorder.com/";

  public static final String JSON_FILTER_NAME = "jiaOrderFilterName";

  public static final String FILE_SERVER_DOMAIN = "../../";

  public static final String PRODUCT_DEFAULT_IMG = "/uploads/images/product.png";

  public static final String PROJECT_TITLE = "加加订货，货订天下";

  public static final String URL_PRODUCT = "/mobile/product/product.jsp";
  public static final String URL_CART = "/mobile/cart/cart.jsp";
  public static final String URL_ORDER = "/mobile/order/orderList.jsp";
  public static final String URL_MY = "/mobile/my/index.jsp";

  public static final String GENERATED_EXCEL_FILE = "/download/file/GeneratedExcelFile/";

  public static final String MODEL_EXCEL_FILE="/download/file/ModelExcelFile/";


  public static final String URL_MOBILE_TIYAN = URL_DOMAIN +"mobile/login.jsp";



  //微信相关配置
  public static final String WX_APPID = "wxa9170241098faaea";
  public static final String WX_TOKEN = "jiaorder1q2w3e4r";
  public static final String WX_SECRET = "3df537dc5211aa26da9881eddd31dcd4";


  //微信模版消息ID
  public static final String WX_TEMPLATE_MSG_TO_ADMIN_ID = "6NwYM7xBfmC4qQve4GSBF2c6nXYkE3HN3VyngXnsWTI";





  //获取公众号token的赋权类型
  public static final String WX_TOKEN_GRANT_TYPE = "client_credential";

  public static final String GZH = "jiaOrder";


  public static final String WX_QRCODE_SAVE_PATH = "D:\\site\\app\\ROOT\\uploads\\qrcode\\";


  public static final String WX_DEBUG_ACCESS_TOKEN = "3FSc8arSgIwWp6h1TnFQign4FRpFshawTfR4tnnsqaIlqK1G1dSoEeyVNjraHF9ZZJfRRZamX3x-uUSiZD-LMol8aC7imN4MJ-_NEk4eAZnSFpEWo_E9gBNRSr6ChZ3yVUWjAJAHGI";

  public static final String WX_SUNSAI_OPENID = "oYWo6t2KlUQsCc4F-n1fZ585pvA4";
  public static final String WX_DINGKUN_OPENID = "oYWo6t_FH7LRNu0d6QNkvAcud__8";

  public static String WX_ADMIN_OPENID = "o61Cmv7pHv4NtRIY9Cc9B7yomXYc,o61Cmv3BPxPjWh9bpiWWAMYPzmC0,o61Cmv898ytqeP3yDTMNtDOMtJDE"; //

  //public static String WX_ADMIN_OPENID = "o61Cmv7pHv4NtRIY9Cc9B7yomXYc"; //赛




  public static final String AUTH_IS_LOGIN_CAN_USED_RES = "has_login_can_use_res";//权限验证，登录就可以使用的资源

  public static final String WX_NOTIFY_NEW_ORDER = "4|wx_notify_new_order";
  public static final String WX_NOTIFY_ORDER_CW_CHECK = "4|wx_notify_order_cw_check";
  public static final String WX_NOTIFY_ORDER_CK_CHECK = "4|wx_notify_order_ck_check";
  public static final String WX_NOTIFY_ORDER_FH_CHECK = "4|wx_notify_order_fh_check";
  public static final String WX_NOTIFY_ORDER_SH_CHECK = "4|wx_notify_order_sh_check";


}
