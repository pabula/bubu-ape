package com.pabula.common.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 文件处理助手
 * Created by Pabula on 16/6/28 12:08.
 */
public class FileUtil {

    /**
     * 读取出文件的内容
     * @param Path
     * @return
     */
    public static String readFile(String Path){
        BufferedReader reader = null;
        String laststr = "";
        try{
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
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




}
