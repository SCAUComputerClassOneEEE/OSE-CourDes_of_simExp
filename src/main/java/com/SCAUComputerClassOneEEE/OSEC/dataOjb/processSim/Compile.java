package com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhl、hlf
 * @date 11/10/2020
 */
public class Compile {
    public static String decompile(char result) {
        // 然后通过 Integer 中的 toBinaryString 方法来一个一个转
        String binStr = Integer.toBinaryString(result);
        StringBuffer s = new StringBuffer();
        for (int j = binStr.length(); j < 8; j++) {
            s.append("0");
        }
        s.append(binStr);
        String r = s.toString();
        /**
         *  8个bit，前三位为操作码
         *  000自加，001自减，后面全为0
         *  010为申请设备，00为A设备，01为B设备，10为C设备，后3位为使用时间
         *  011为赋值,后面为赋值数据
         *  100为end，后面全为0
         */

        String op = r.substring(0, 3);
        switch (op) {
            case "000":
                return "X++";
            case "001":
                return "X--";
            case "100":
                return "end";
            case "010":
                String part1 = r.substring(3, 5);
                char equipment = (char) (B2D(part1) + 65);
                String part2 = r.substring(5);
                int time = B2D(part2);
                return "!" + equipment + time;
            case "011":
                int decimal = B2D(r.substring(3));
                return "X=" + decimal;
        }
        return null;
    }

    public static String compile(String result) {
        int machineCode = 0;
        StringBuilder contents = new StringBuilder();
        Pattern format1 = Pattern.compile("(^[a-zA-Z]+)(\\++|--)");//匹配自（加/减）
        Matcher matcher1;

        Pattern format2 = Pattern.compile("!([A|B|C])(\\d{1,2})");//申请设备
        Matcher matcher2;

        String format3 = "end";

        Pattern format4 = Pattern.compile("(^[a-zA-Z]+)=(\\d{1,2})"); //匹配赋值语句
        Matcher matcher4;
        matcher1 = format1.matcher(result);    //自（加/减）
        if (matcher1.matches()) {
            String action = matcher1.group(2);
            if ("++".equals(action)) contents.append((char) 0);
            else contents.append((char) 32);
        }

        matcher2 = format2.matcher(result);    //申请设备
        if (matcher2.matches()) {
            String deviceName = matcher2.group(1);
            int deviceCode = deviceName.charAt(0) - 65;
            machineCode = Integer.parseInt(matcher2.group(2)) + 64 + deviceCode * 8;
            contents.append((char) machineCode);
        }

        if (result.matches(format3))       //end
            contents.append((char) 128);

        matcher4 = format4.matcher(result);    //赋值语句
        if (matcher4.matches()) {
            int num = Integer.parseInt(matcher4.group(2));
            contents.append((char) (num + 96));
        }
        return contents.toString();
    }

    private static int B2D(String binary) {
        int decimal = (int) Long.parseLong(binary, 2);
        return decimal;
    }

    public static void main(String[] args) {
        String result = Compile.decompile(Compile.compile("X=6").charAt(0));
        System.out.println(result);

    }
}
