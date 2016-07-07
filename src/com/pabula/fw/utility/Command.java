package com.pabula.fw.utility;

import com.alibaba.fastjson.JSONObject;
import com.pabula.common.util.ValidateUtil;
import com.pabula.fw.exception.DataAccessException;
import com.pabula.fw.exception.RuleException;
import com.pabula.fw.exception.SysException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;


public interface Command {

  /**
   * 主执行方法
   * @param requestData
   * @param command
   * @param commandConfig
   * @param validate
   * @return
   * @throws DataAccessException
     */
  String main(RequestData requestData, Command command, JSONObject commandConfig, ValidateUtil validate) throws DataAccessException;

  /**
   * 装载数据
   * @return
   * @throws SysException
   */
  String initdata(RequestData $DATA) ;

  /**
   * 数据校验
   * @param $DATA
   * @param validate
   * @throws RuleException
   */
  void validate(RequestData $DATA, ValidateUtil validate);

  /**
   * 执行业务
   * @param $DATA
   * @param request
   * @return
   * @throws ServletException
   * @throws DataAccessException
   */
  String execute(RequestData $DATA, HttpServletRequest request) throws DataAccessException;


}
