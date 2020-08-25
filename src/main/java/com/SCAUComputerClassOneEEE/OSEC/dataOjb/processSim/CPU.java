package com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hlf
 * @date 25/8/2020
 */
public class CPU {
    public static String IR;
    public static int PC = 0;
    public static Map<String,Integer> map = new HashMap<>();
    public static Pattern format1 =Pattern.compile("(^[a-zA-Z]+)(\\++|--)");//匹配自（加/减）
    public static Matcher matcher1;

    public static Pattern format2 =Pattern.compile("!([A|B|C])(\\d{1,2})");
    public static Matcher matcher2;

    public static String format3 ="end";
    public static Pattern format4 =Pattern.compile("(^[a-zA-Z]+)=(\\d{1,2})"); //匹配赋值语句
    public static Matcher matcher4;

    public static void main(String[] args) {
        File file = new File("/Users/apple/Desktop/test.txt");
        ArrayList<String> contents = new ArrayList<>();
        type(file,contents);
        for(String each:contents){
            System.out.println(each);

            matcher1 = format1.matcher(each);
            if(matcher1.matches()){
                String name = matcher1.group(1);
                String action = matcher1.group(2);
                int value = map.get(name);
                if(action.equals("++")){
                    value++;
                }else{
                    value--;
                }
                PC++;
                map.put(name,value);
                System.out.println(name+":"+value);
            }

            matcher2 = format2.matcher(each);
            if(matcher2.matches()){
                String deviceName = matcher2.group(1);
                int time = Integer.parseInt(matcher2.group(2));
                System.out.println(deviceName+":"+time);
                PC++;
            }

            if(each.matches(format3)){
                PC++;
            }

            matcher4 = format4.matcher(each);
            if(matcher4.matches()){
                String name = matcher4.group(1);
                int num = Integer.parseInt(matcher4.group(2));
                map.put(name,num);
                PC++;
            }
            System.out.println("正在执行第"+PC+"条指令");
            System.out.println("-------");
        }

    }
    public static void type(File exeFile, ArrayList<String> contents) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(exeFile));
            String s = null;
            while ((s = br.readLine()) != null) {
                contents.add(s);
                IR = s;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

