package com.altas.gateway.utils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangy on 2017/7/7.
 */
public class ConvertUtils {

    public static int getWasteTimeMinutes(final int wasteTime){

        if(wasteTime<=0)
            return 0;

        BigDecimal bigDecimalMin = new BigDecimal((double) wasteTime /60000d);

        return bigDecimalMin.setScale(0, BigDecimal.ROUND_UP).intValue();
    }
    /**
     *
     * @param type
     * @param string
     * @return
     */
    public static Object forceTypeConvert(Class<?> type,String string)  {
        try {
            if (type.equals(String.class)) {
                return string;
            } else if (type.equals(Integer.class)||type.equals(int.class)) {
                Integer integer = Integer.valueOf(string);
                return integer;
            } else if (type.equals(Byte.class)||type.equals(byte.class)) {
                Byte aByte = Byte.valueOf(string);
                return aByte;
            } else if (type.equals(Boolean.class)||type.equals(boolean.class)) {
                Boolean aBoolean = Boolean.valueOf(string);
                return aBoolean;
            } else if (type.equals(Character.class)||type.equals(char.class)) {
                Character character = string.toCharArray()[0];
                return character;
            } else if (type.equals(Short.class)||type.equals(short.class)) {
                Short aShort = Short.valueOf(string);
                return aShort;
            } else if (type.equals(Long.class)||type.equals(long.class)) {
                Long aLong = Long.valueOf(string);
                return aLong;
            } else if (type.equals(Float.class)||type.equals(float.class)) {
                Float aFloat = Float.valueOf(string);
                return aFloat;
            } else if (type.equals(Double.class)||type.equals(double.class)) {
                Double aDouble = Double.valueOf(string);
                return aDouble;
            } else if (type.equals(Date.class)) {
                Date date = DateFormat.getInstance().parse(string);
                return date;
            }  else {

            }
        }catch (Exception ex){
        }
        return null;
    }

    public static String convertList(List<String> list, String splitChar){

        if(null == list || list.isEmpty())
            return "";

        StringBuilder stringBuilder = new StringBuilder();
        for(String str : list){
            stringBuilder.append(str);
            stringBuilder.append(splitChar);
        }

        return stringBuilder.substring(0, stringBuilder.length() - splitChar.length());
    }

    public static String getWeekOfDate(Date date) {
        String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        if(date != null){
            calendar.setTime(date);
        }
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0){
            w = 0;
        }
        return weekOfDays[w];
    }
}
