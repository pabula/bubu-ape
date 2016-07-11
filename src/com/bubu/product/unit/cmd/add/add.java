package com.bubu.product.unit.cmd.add;

import com.pabula.common.util.ValidateUtil;
import com.pabula.fw.cmd.CommonCommand;
import com.pabula.fw.exception.DataAccessException;
import com.pabula.fw.utility.RequestData;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Pabula on 16/6/29 14:58.
 */
public class add extends CommonCommand{

    @Override
    public String initdata(RequestData $DATA) {
        return null;
    }

    @Override
    public void validate(RequestData $DATA, ValidateUtil validate) {

    }

    @Override
    public String execute(RequestData $DATA, HttpServletRequest request) throws DataAccessException {
        return null;
    }
}
