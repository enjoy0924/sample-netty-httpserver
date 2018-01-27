package com.alr.gateway.utils;

/**
 * Created by G_dragon on 2016/10/14.
 */

import java.math.BigDecimal;

/**
 * 为避免金额计算精度丢失，将double类型转换为BigDecimal再进行相关计算
 *
 * @author kang
 *
 */
public class BigDecimalUtils {

    /**
     * 向上进位
     *
     * @author: Gao Peng
     * @date: 2016年6月21日 下午3:06:55
     * @param: @param
     *             newScale
     * @param: @param
     *             b
     * @param: @return
     * @return: BigDecimal
     */
    public static BigDecimal setScaleRoundUp(int newScale, BigDecimal b) {
        return b.setScale(newScale, BigDecimal.ROUND_UP);
    }

    /**
     *
     * @author: Gao Peng
     * @date: 2016年6月21日 下午5:18:41
     * @param: @param
     *             newScale
     * @param: @param
     *             b
     * @param: @return
     * @return: BigDecimal
     */
    public static BigDecimal setScaleRoundDown(int newScale, BigDecimal b) {
        return b.setScale(newScale, BigDecimal.ROUND_DOWN);
    }

    /**
     *
     * @author: Gao Peng
     * @date: 2016年6月21日 下午5:18:44
     * @param: @param
     *             newScale
     * @param: @param
     *             b
     * @param: @return
     * @return: BigDecimal
     */
    public static BigDecimal setScaleRoundCeiling(int newScale, BigDecimal b) {
        return b.setScale(newScale, BigDecimal.ROUND_CEILING);
    }

    /**
     *
     * @author: Gao Peng
     * @date: 2016年6月21日 下午5:18:47
     * @param: @param
     *             newScale
     * @param: @param
     *             b
     * @param: @return
     * @return: BigDecimal
     */
    public static BigDecimal setScaleRoundFloor(int newScale, BigDecimal b) {
        return b.setScale(newScale, BigDecimal.ROUND_FLOOR);
    }

    /**
     *
     * @author: Gao Peng
     * @date: 2016年6月21日 下午5:18:50
     * @param: @param
     *             newScale
     * @param: @param
     *             b
     * @param: @return
     * @return: BigDecimal
     */
    public static BigDecimal setScaleRoundHalfUp(int newScale, BigDecimal b) {
        return b.setScale(newScale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     *
     * @author: Gao Peng
     * @date: 2016年6月21日 下午5:18:53
     * @param: @param
     *             newScale
     * @param: @param
     *             b
     * @param: @return
     * @return: BigDecimal
     */
    public static BigDecimal setScaleRoundHalfDown(int newScale, BigDecimal b) {
        return b.setScale(newScale, BigDecimal.ROUND_HALF_DOWN);
    }

    /**
     *
     * @author: Gao Peng
     * @date: 2016年6月21日 下午5:18:56
     * @param: @param
     *             newScale
     * @param: @param
     *             b
     * @param: @return
     * @return: BigDecimal
     */
    public static BigDecimal setScaleRoundHalfEven(int newScale, BigDecimal b) {
        return b.setScale(newScale, BigDecimal.ROUND_HALF_EVEN);
    }

    /**
     *
     * @author: Gao Peng
     * @date: 2016年6月21日 下午5:18:59
     * @param: @param
     *             newScale
     * @param: @param
     *             b
     * @param: @return
     * @return: BigDecimal
     */
    public static BigDecimal setScaleRoundUnnecessary(int newScale, BigDecimal b) {
        return b.setScale(newScale, BigDecimal.ROUND_UNNECESSARY);
    }

    public static Double roundDoubleNum(int precision, String value){
        if(null == value){
            return null;
        }
        return new BigDecimal(value).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static Double roundDoubleNum(int precision, double value){
        return new BigDecimal(value).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}