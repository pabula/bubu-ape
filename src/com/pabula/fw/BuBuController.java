package com.pabula.fw;

import com.alibaba.fastjson.JSONObject;
import com.pabula.common.util.StrUtil;
import com.pabula.common.util.ValidateUtil;
import com.pabula.fw.cmd.CommonCommand;
import com.pabula.fw.exception.DataAccessException;
import com.pabula.fw.exception.RuleException;
import com.pabula.fw.exception.SysException;
import com.pabula.fw.utility.Command;
import com.pabula.fw.utility.RequestData;
import org.apache.log4j.Logger;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;


/**
 * 实现rest风格的控制器
 * BUBU FRAMEWORK by pabula 2016-06-27	18:19
 */
public class BuBuController extends HttpServlet {

	Logger log = Logger.getLogger(BuBuController.class);

	private static HashMap commandClassCache = new HashMap();
	//所有JSON配置文件的缓存
	private static HashMap configJsonCache = new HashMap();

	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";


	//业务主包名称
	String classPath = "";
	//业务实体名称
	String className = "";
	//根的业务包
	String rootPackages = "";


	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType(CONTENT_TYPE);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Charset", "UTF-8");
		request.setCharacterEncoding("UTF-8");

		processRequest(request, response);
	}

	public void doGet(HttpServletRequest request,HttpServletResponse response)	throws ServletException, IOException {
		response.setContentType(CONTENT_TYPE);
		response.setCharacterEncoding("utf-8");

		request.setCharacterEncoding("UTF-8");
		processRequest(request, response);
	}

	public void init(ServletConfig arg0) throws ServletException {
		super.init(arg0);
	}

	/**
	 * 挂过滤器。 重载service(ServletRequest, ServletResponse)方法，判断用户是否有权限访问资源
	 */

	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		super.service(req, res);
	}

	/**
	 * 处理
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//CMD返回的值
		String cmdReturnStr = null;


		/********************************
		 * 初始化数据:根据请求,分析出这个请求的意义,要请求哪个业务包,哪个CMD去接收这个请求
		 *********************************/
		initBusiPath(request);


		/********************************
		 * 初始化数据:将所有请求中的对象,封装在一个对象中,方便向command中传递
		 *********************************/
		RequestData requestData = new RequestData(request, response);


		/********************************
		 * 以下2句话可以防止网页被缓存（如代理服务器等）
		 *********************************/
		//requestHelper.getRequest().setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		
		try {

			/********************************
			 * 根据请求,找到对应的COMMAND
			 *********************************/
			Command command = getCommand(classPath,className);
			ValidateUtil validate = new ValidateUtil();

			/********************************
			 * 根据请求,读取COMMAND对应的 JSON CONFIG
			 *********************************/
			JSONObject commandJsonObject = this.getConfig(command,classPath,className);


			/********************************
			 * 执行command
			 *********************************/
			cmdReturnStr = command.main(requestData,command,commandJsonObject,validate);


			/********************************
			 * 校验数据合法性
			 *********************************/
			if (validate.hasError()) {
				RuleException e = new RuleException();
				e.setErrColl(validate.getErrors());
				throw e;
			}


			/********************************
			 * COMMAND 返回值的处理
			 *********************************/
//			if (next != null && action.indexOf("Ajax")<0) {	//非AJAX请求，则回返一个URL，进行跳转
//				dispatch(request, response, next);
//			}else if(action.indexOf("Ajax")>=0){	//AJAX请求，直接输入JSON内容至前台

				//AJAX式返回

				response.setContentType("application/json");
				response.getWriter().print(cmdReturnStr);
				response.getWriter().flush();
				response.getWriter().close();
//			}


			
		} catch (Exception e) {

			/********************************
			 * RuleException 输入规则异常
			 *********************************/
			if (e instanceof RuleException || e instanceof DataAccessException){

				//如果表单验证出错，则返回错误提示的json数据
				String errorMessage = StrUtil.strCollToStr(((RuleException)e).getErrColl(),",");

				//拼接成json，防止拼接的过程中出错
				String errorJson = "{\"code\":4000,\"msg\":\""+ errorMessage +"\",\"data\":\"error\"}";

				response.setContentType("application/json");
				response.getWriter().print(errorJson);
				response.getWriter().flush();
				response.getWriter().close();

				return;
			}else if (e instanceof ServletException) {
				throw (ServletException) e;
			}
			
			e.printStackTrace();
			log.error(e.getMessage());
			log.error(e.getStackTrace().toString());

		}
	}


	/**
	 * 根据请求,分析出这个请求的意义,要请求哪个业务包,哪个CMD去接收这个请求
	 * @param request
     */
	private void initBusiPath(HttpServletRequest request){

		//得到项目当前的文件路径
//		String realPath = this.getServletContext().getRealPath("/");

		//根的业务包
		rootPackages = this.getServletConfig().getInitParameter("packages");
		String context = this.getServletConfig().getInitParameter("context");	//URL的上下文

		//请求的URL类似  /bubu/jiaorder/product/unit/add
		String requestURI = request.getRequestURI();
		String restURL = requestURI.substring(requestURI.indexOf(context)+context.length());

		//业务主包名称
		classPath = restURL.substring(0,restURL.lastIndexOf("/"));

		//业务实体名称
		className = restURL.substring(restURL.lastIndexOf("/"));

	}



	/**
	 * 读取.json文件,并返回一个 JSONObject对象
	 * @return
	 */
	private JSONObject getConfig(Command command,String packagePath,String busiName){

		String jsonFilePath = packagePath + busiName;

		JSONObject jsonObject = new JSONObject();

		if(configJsonCache.containsKey(jsonFilePath)){        //如果存在于缓存中,则直接返回

			jsonObject = (JSONObject)configJsonCache.get(jsonFilePath);

		}else{

			String JsonContext = readCommandConfigFile(command);	//读取配置文件
			jsonObject = JSONObject.parseObject(JsonContext);

			//添加进缓存
			configJsonCache.put(jsonFilePath,jsonObject);

		}

		return jsonObject;
	}


	/**
	 * 读取COMMAND对应的配置文件
	 * @param command
	 * @return
     */
	private  String readCommandConfigFile(Command command){
		BufferedReader reader = null;
		String laststr = "";
		try{
			InputStream is = command.getClass().getResourceAsStream("config.json");
			InputStreamReader inputStreamReader = new InputStreamReader(is, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while((tempString = reader.readLine()) != null){
				laststr += tempString;
			}
			reader.close();
		}catch(IOException e){
			laststr = "";   //如果出现异常,则清空所有读取的数据
			e.printStackTrace();
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return laststr;
	}



	/**
	 * 动态创建COMMAND
	 * @return
	 * @throws SysException
     */
	private Command getCommand(String packagePath,String busiName) throws SysException {

		Command cmd = null;

		String className = packagePath + ".cmd." + busiName;
		System.err.println("动态command路径： " + className);

		//先从缓存中读取
		if(commandClassCache.containsKey(className)){

			cmd = (Command) commandClassCache.get(className);

		}else{	//缓存没有时,重新加载
			try {
				//动态创建vo类
				cmd = (Command) Class.forName(className).newInstance();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			if (cmd == null) {
				throw new SysException("命令请求无法受理，请检查请求的action是否正确 " + className);
			}

			commandClassCache.put(className,cmd);
		}


		return cmd;

	}

	/**
	 * returns a description of the servlet
	 */
	public String getServletInfo() {
		return getSignature();
	}


	/**
	 * 页面跳转式
	 * @param request
	 * @param response
	 * @param page
	 * @throws ServletException
	 * @throws IOException
     */
	private void dispatch(HttpServletRequest request,
			HttpServletResponse response, String page) throws ServletException,
			IOException {

//		//		RequestDispatcher dispatcher = getServletContext()
//		//				.getRequestDispatcher(page);
//
//		//RELOAD代表着重新回到原始的请示URL中，并刷新它
//		if(page.equals("RELOAD")){
//			page = "/common/redirect.jsp";
//			String returnUrl = request.getHeader("Referer");
//			request.setAttribute("RELOAD",returnUrl);
//		}
//
//		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
//
//		//设置BASE_HREF属性，以供page使用。解决服务器端URL与设计时URL混乱的问题
//		String context = request.getContextPath();	//上下文 如JCMS
//		String host = request.getServerName();		//服务器主机名
//		int port = request.getServerPort();			//端口号
//		String siteUrl = "http://" + host + ":" + port + context; //完整的站点URL
//
//		request.setAttribute("BASE_HREF", siteUrl + page);
//		//System.err.println("host: " + host + " Context " + context + " port: " + port);
//
//
//
//		//下面可以得到原始的URL请示路径及search
////		System.err.println("Referer +++++++++++++ " + request.getHeader("Referer"));
//
//		dispatcher.forward(request, response);
	}

	private String getSignature() {
		return "bubu framework v1";
	}

	
}