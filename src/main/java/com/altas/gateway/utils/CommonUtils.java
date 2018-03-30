package com.altas.gateway.utils;

import com.alr.core.utils.DateTimeHelper;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Author: syd
 * Date:2017/7/17
 * Description:
 */
public class CommonUtils {

    private static String[] validateCodes = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

    //生成六位随机验证码
    public static String getRandomCodeOf6() {

        List list = Arrays.asList(validateCodes);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        String result = afterShuffle.substring(3, 9);
        return result;
    }


    public static boolean isUUID(String uuidStr) {
        if (StringUtils.isEmpty(uuidStr)) {
            return false;
        }
        if (uuidStr.trim().length() != 36) {
            return false;
        }
        String[] uuidCut = uuidStr.trim().split("-");
        if (uuidCut.length != 5) {
            return false;
        }
        return true;
    }




    public static final String convertNumToChinese(int num){
        String[] chineseNum = "一 二 三 四 五 六 七 八 九 十".split(" ");
        if(num <= 0){
            return "";
        } else if(num <= 10){
            return chineseNum[num - 1];
        } else {
            String c = chineseNum[num%10 - 1];
            //超过二十，需要把
            if(num/10 > 1){
                c = chineseNum[num/10 - 1] + "十" + c;
            } else { //介于10-20之间则不需要第一个字符，如11为十一而不是一十一
                c = "十" + c;
            }
            return c;
        }
    }




}
