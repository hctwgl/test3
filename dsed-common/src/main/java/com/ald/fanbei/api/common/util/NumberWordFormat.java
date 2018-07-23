package com.ald.fanbei.api.common.util;


import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author chefeipeng 2017年1月16日 下午11:44:10
 * @类描述：用户工具类
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("numberWordFormat")
public class NumberWordFormat {
    public static final String ZERO = "zero";
    public static final String NEGATIVE = "negative";
    public static final String SPACE = " ";
    public static final String MILLION = "million";
    public static final String THOUSAND = "thousand";
    public static final String HUNDRED = "hundred";
    public static final String[] INDNUM = {"zero", "one", "two", "three", "four", "five", "six",
            "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen",
            "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"};
    public static final String[] DECNUM = {"twenty", "thirty", "forty", "fifty", "sixty",
            "seventy", "eighty", "ninety"};

    /**
     * 借款时间
     *
     * @param
     * @return
     */
    public int borrowTime(final String type) {
        Integer day ;
        if(isNumeric(type)){
            day = Integer.parseInt(type);
        }else{
            day = parse(type.toLowerCase());
        }
        return day;
    }

    /**
     * 是否是数字字符串
     * @param type
     * @return
     */
    public boolean isNumeric(String type) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(type);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    //数字转换英文
    public String format(int i) {

        StringBuilder sb = new StringBuilder();

        if(i == 0) {
            return ZERO;
        }

        if(i < 0) {
            sb.append(NEGATIVE).append(SPACE);
            i *= -1;
        }


        if(i >= 1000000) {
            sb.append(numFormat(i / 1000000)).append(SPACE).append(MILLION).append(SPACE);
            i %= 1000000;

        }

        if(i >= 1000) {
            sb.append(numFormat(i / 1000)).append(SPACE).append(THOUSAND).append(SPACE);

            i %= 1000;
        }

        if(i < 1000){
            sb.append(numFormat(i));
        }

        return sb.toString();
    }

    // 3位数转英文
    public String numFormat(int i) {

        StringBuilder sb = new StringBuilder();

        if(i >= 100) {
            sb.append(INDNUM[i / 100]).append(SPACE).append(HUNDRED).append(SPACE);
        }

        i %= 100;

        if(i != 0) {
            if(i >= 20) {
                sb.append(DECNUM[i / 10 -2]).append(SPACE);
                if(i % 10 != 0) {
                    sb.append(INDNUM[i % 10]);
                }
            }else {
                sb.append(INDNUM[i]);
            }
        }

        return sb.toString();
    }
    //英文转数字
    public int parse(String str) {
        HashMap<String, Integer> hm = new HashMap<String, Integer>();
        hm.put("zero", 0);
        hm.put("one", 1);
        hm.put("two", 2);
        hm.put("three", 3);
        hm.put("four", 4);
        hm.put("five", 5);
        hm.put("six", 6);
        hm.put("seven", 7);
        hm.put("eight", 8);
        hm.put("nine", 9);
        hm.put("ten", 10);
        hm.put("eleven", 11);
        hm.put("twelve", 12);
        hm.put("thirteen", 13);
        hm.put("fourteen", 14);
        hm.put("fifteen", 15);
        hm.put("sixteen", 16);
        hm.put("seventeen", 17);
        hm.put("eighteen", 18);
        hm.put("nineteen", 19);
        hm.put("twenty", 20);
        hm.put("thirty", 30);
        hm.put("forty", 40);
        hm.put("fifty", 50);
        hm.put("sixty", 60);
        hm.put("seventy", 70);
        hm.put("eighty", 80);
        hm.put("ninety", 90);
        hm.put("hundred", 100);
        hm.put("thousand", 1000);
        hm.put("million", 1000000);
        int i = 0;
        int b = 0;
        int c = 0;
        String[] k = str.split(" ");
        for (String string : k) {
            if("hundred".equals(string)){
                i *= hm.get("hundred");
            }else if("thousand".equals(string)){
                b = i;
                b *= hm.get("thousand");
                i = 0;
            }else if("million".equals(string)){
                c = i;
                c *= hm.get("million");
                i = 0;
            }else if("negative".equals(string)){
                i = 0;
            }else {
                i += hm.get(string);
            }
        }
        i += c + b;
        for (String string2 : k) {
            if("negative".equals(string2)){
                i = -i;
            }
        }
        return i;
    }
    public static void main(String[] args) {
//          NumberWordFormat nwf = new NumberWordFormat();
//          System.out.println(nwf.format(1432008));
//          int  i = nwf.parse("one million four hundred thirty two thousand eight");
//          System.out.println("i="+i);
        NumberWordFormat numberWordFormat = new NumberWordFormat();
        Boolean flag = false;
        Scanner sc = new Scanner(System.in);
        try {
            do {
                System.out.println("请选择format还是parse，format输入1，parse输入2");
                String input = sc.nextLine();
                if("1".equals(input)){
                    System.out.println("您选择了format，请输入数字 （注：范围在999999999和-999999999之间）:");
                    input = sc.nextLine();
                    int num = Integer.parseInt(input);
                    if(num > 999999999 || num< -999999999 || input.length() > String.valueOf(num).length()){
                        throw new NumberFormatException();
                    }
                    System.out.println("转换结果为： " + numberWordFormat.format(num));
                }else if("2".equals(input)){
                    System.out.println("您选择了parse，请输入英文数字,注意格式（每输入一个单词后一空格）:");
                    input = sc.nextLine();
                    try {
                        System.out.println("转换结果为： " + numberWordFormat.parse(input));
                    } catch (Exception e) {
                        System.out.println("请输入正确的英文单词及格式!!重新输入!!");
                    }
                }else {
                    System.out.println("输入错误，请重新输入！！");
                }
                System.out.println("是否继续？<Y/N>");
                input = sc.nextLine();
                if("Y".equalsIgnoreCase(input)){
                    flag = true;
                }else {
                    flag = false;
                }
            } while (flag);
        } finally {
            sc.close();
        }
    }

}
