package com.bubu.product.unit.cmd.add;

import com.pabula.common.util.ValidateUtil;
import com.pabula.fw.cmd.CommonCommand;
import com.pabula.fw.exception.DataAccessException;
import com.pabula.fw.utility.RequestData;
import com.pabula.fw.utility.ResponseData;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Pabula on 16/6/29 14:58.
 */
public class add extends CommonCommand{

    @Override
    public void initdata(RequestData $DATA) {
        $DATA.put("unit.class_name","中国人");
    }

    @Override
    public void validate(RequestData $DATA, ValidateUtil validate) {
        System.err.println("这是自己CMD的 validate");
        //validate.addError($DATA.getValueFromVar("$data.unit.name"));
    }

    @Override
    public void execute(RequestData $DATA, HttpServletRequest request, ResponseData responseData) throws DataAccessException {
        System.err.println("这是自己CMD的EXECUTE");
        $DATA.put("unit.class_name","中国人2222");
    }
}
