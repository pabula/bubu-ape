package com.pabula.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sunsai on 2016/5/10 - 12:59.
 */
public class Lang {

  private Lang(ResourceBundle message) {
    this.message = message;
  }

  public static final ThreadLocal<String> LANGUAGE = new ThreadLocal<>();

  public static final ThreadLocal<String> COUNTRY = new ThreadLocal<>();

  public static void setLanguage(String strLanguage) {
    LANGUAGE.set(strLanguage);
  }

  public static void setCountry(String strCountry) {
    COUNTRY.set(strCountry);
  }

  private static Map<String, ResourceBundle> map = new ConcurrentHashMap<>();

  public static Lang lang(String lang, String country) {
    return lang(new Locale(lang, country));
  }

  public static Lang lang(String lang) {
    return lang(lang, "");
  }

  public static Lang lang() {

    String lang = LANGUAGE.get();
    String coun = COUNTRY.get();

    if (StrUtil.isNull(lang)) {
      lang = "zh";
    }

    if (StrUtil.isNull(coun)) {
      coun = "CN";
    }

    return lang(lang, coun);
  }

  public static synchronized Lang lang(Locale locale) {

    String language = locale.getLanguage();

    String country = locale.getCountry();

    String key = language +"_" + country;
    ResourceBundle message = map.get(key);

    if (message == null) {
      message = ResourceBundle.getBundle("com.pabula.common.util.properties.lang", locale, new LanguageControl("GBK"));
      //map.put(key, message);
    }
    return new Lang(message);
  }

  private ResourceBundle message;



  public String getString(String key) {
    return message.getString(key);
  }

  public String getLanguage() {
    return message.getLocale().getLanguage();
  }




}


class LanguageControl extends ResourceBundle.Control {

  private String encode = "GBK";

  public LanguageControl(String encode) {
    this.encode = encode;
  }

  public ResourceBundle newBundle
      (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
      throws IllegalAccessException, InstantiationException, IOException
  {
    // The below is a copy of the default implementation.
    String bundleName = toBundleName(baseName, locale);
    String resourceName = toResourceName(bundleName, "properties");
    ResourceBundle bundle = null;
    InputStream stream = null;
    if (reload) {
      URL url = loader.getResource(resourceName);
      if (url != null) {
        URLConnection connection = url.openConnection();
        if (connection != null) {
          connection.setUseCaches(false);
          stream = connection.getInputStream();
        }
      }
    } else {
      stream = loader.getResourceAsStream(resourceName);
    }
    if (stream != null) {
      try {
        // Only this line is changed to make it to read properties files as UTF-8.
        bundle = new PropertyResourceBundle(new InputStreamReader(stream, encode));
      } finally {
        stream.close();
      }
    }
    return bundle;
  }
}

