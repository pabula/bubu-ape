package com.pabula.common.util;

import com.sun.jna.platform.win32.OaIdl;
import java.util.Date;

/**
 * Created by sunsai on 2016/3/10.
 */
public class MoneyUtil {

  /**
   *
   * @param money 单位为分的整数
   * @return 返回保留两位小数的字符串， 单位元
   */
  public static String printMoney(int money) {

    int little = money % 100;

    //下面注释掉的这行代码竟然比没注释的效率慢，估计和三元操作符的返回类型推导有关
    //return money / 100 + "." + (little < 10 ? "0" + little : little);
    return money / 100 + "." + (little < 10 ? "0" + little : "" + little);
  }

  public static void main(String[] args) {

    long start = new Date().getTime();
    for (int i = 0; i < 200; i++) {
      System.out.println(printMoney(i));
    }
    long end = new Date().getTime();

    System.out.println(end - start);
  }

}
