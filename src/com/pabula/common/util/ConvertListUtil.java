package com.pabula.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunsai on 2016/6/1 - 20:22.
 */
public class ConvertListUtil<T> {


  public Map<Integer, T> convertListToMapByIntKey(List<T> list, Class clazz, String keyFieldName) {

    Map<Integer, T> result = new HashMap<>();

    for (T object : list) {
      try {
        Field field = clazz.getDeclaredField(keyFieldName);
        field.setAccessible(true);
        result.put(field.getInt(object), object);
      } catch (Exception e) {
        e.printStackTrace();
        return new HashMap<>();
      }
    }
    return result;
  }



  public Map<String, T> convertListToMapByStringKey(List<T> list, Class clazz, String keyFieldName) {

    Map<String, T> result = new HashMap<>();

    for (T object : list) {
      try {
        Field field = clazz.getDeclaredField(keyFieldName);
        field.setAccessible(true);
        result.put(field.get(object).toString(), object);
      } catch (Exception e) {
        e.printStackTrace();
        return new HashMap<>();
      }
    }
    return result;
  }



  public Map<Integer, List<T>> convertListToMapList(List<T> list, Class clazz, String keyFieldName) {

    Map<Integer, List<T>> result = new HashMap<>();

    for (T object : list) {
      try {
        Field field = clazz.getDeclaredField(keyFieldName);
        field.setAccessible(true);
        int key = field.getInt(object);
        List<T> tmpList = result.get(key);
        if (null == tmpList) {
          tmpList = new ArrayList<>();
          result.put(key, tmpList);
        }
        tmpList.add(object);
      } catch (Exception e) {
        e.printStackTrace();
        return new HashMap<>();
      }
    }
    return result;
  }



}
