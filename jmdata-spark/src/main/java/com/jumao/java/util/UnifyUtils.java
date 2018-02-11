package com.jumao.java.util;

/**
 * @description:
 * @author: tanyikuang
 * @since: 2016/10/19
 * @history:
 */
public class UnifyUtils {


    /**
     * 统一字符串的长度
     *
     * @param source  源字符串
     * @param length  统一的长度
     * @param fillStr 填充的字符串
     * @return 统一长度的字符串
     */
    public static String sameLength(String source, int length, String fillStr) {
        int diff = length - source.length();
        String strs = "";
        if (diff > 0) {
            for (int i = 0; i < diff; i++) {
                strs += fillStr;
            }
        }
        return strs + source;
    }

    /*public static void main(String[] args) {
        String rlt = sameLength("1",2,"0");
        System.out.println(rlt);
        System.out.println(rlt.getBytes().length);
    }
*/
}
