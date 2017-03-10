package com.newcoder.util;

import com.alibaba.fastjson.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Map;

/**
 * Created by nowcoder on 2016/7/3.
 */
public class ToutiaoUtil {
    private static final Logger logger = LoggerFactory.getLogger(ToutiaoUtil.class);
    public static String IMAGR_DOMAIN="127.0.0.1:8080/";
    public static String IMAGE_DIR="E:/头条项目/上传图片/";
    public static String[] IMAGE_FILE_EXTD={"png","bmp","jpg","jpeg"};
    //判断上传文件是否为图片格式
    public static boolean isFileAllowed(String fileName){
        for(String file:IMAGE_FILE_EXTD){
            if(file.equals(fileName)){
                return true;
            }
        }
        return false;
    }
    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            logger.error("生成MD5失败", e);
            return null;
        }
    }
   public static String getJSONString(int code){
        JSONObject json=new JSONObject();
        json.put("code",code);
        return json.toJSONString();
    }
    public static String getJSONString(int code,String msg){
        JSONObject json=new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        return json.toJSONString();
    }
    public static String getJSONString(int code, Map<String,Object> map){
        JSONObject json=new JSONObject();
        json.put("code",code);
        for(Map.Entry<String, Object> entry:map.entrySet()){
            json.put(entry.getKey(),entry.getValue());
        }
        return json.toJSONString();
    }
}
