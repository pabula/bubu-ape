package com.pabula.common.util;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by sunsai on 2016/6/1 - 20:20.
 */
public class CreateIdsUtil<T> {

  public String getIdsByList(List<T> list, Class clazz, String fieldName) {
    StringBuffer sb = null;

    for (Object object : list) {
      if (null == sb) {
        sb = new StringBuffer();
      } else {
        sb.append(",");
      }
      try {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        sb.append(field.getInt(object));
      } catch (Exception e) {
        e.printStackTrace();
        return "";
      }
    }
    return null == sb ? "":sb.toString();
  }














}
