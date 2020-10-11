package com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import com.sun.tools.javac.util.Convert;

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

    public static Pattern format2 =Pattern.compile("!([A|B|C])(\\d{1,2})");//匹配设备
    public static Matcher matcher2;

    public static String format3 ="end";
    public static Pattern format4 =Pattern.compile("(^[a-zA-Z]+)=(\\d{1,2})"); //匹配赋值语句
    public static Matcher matcher4;


    private int psw=0;//程序状态字

    private ArrayList<PCB> blankQueue = new ArrayList<>();//空白队列
    private ArrayList<PCB> readyQueue = new ArrayList<>();//就绪队列
    private ArrayList<PCB> blockedQueue = new ArrayList<>();//阻塞队列

    //预先设置的10个可运行文件，形式仅仅是文件
    private ArrayList<AFile> exeFile = new ArrayList<>();

    /**
     * cpu
     */
    public void cpu(){
        initExeFile();
        while (true){
            switch (psw){
                case 0://无中断
                    processScheduling();
                    break;
                case 1://程序结束中断
                    break;
                case 2://时间片结束中断
                    break;
                    case 3://I/O中断
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 进程调度
     */
    private void processScheduling(){
        AFile executeFile = exeFile.get((int)(10*Math.random()));
        create(executeFile);//创建进程

        //执行指令部分
        for(int i=0;i<6;i++){

        }

    }

    /**进程控制原语
     * 进程申请
     */
    private void create(AFile aFile){
        PCB newProcess = new PCB();//空白进程控制块
        //个人认为PCB中需要文件参数，向内存申请空间时，直接传递可执行文件中的所有字节
        //申请内存
        //填写PCB
        //显示结果
    }

    /**进程控制原语
     * 进程销毁
     */
    private void destroy(){
        //回收内存空间
        //回收PCB
        //显示结果
    }

    /**进程控制原语
     * 进程阻塞
     */
    private void block(){
        //保存运行进程的 CPU 现场
        //修改进程状态
        //将进程链入对应的阻塞队列，然后转向进程调度
    }


    /**进程控制原语
     * 进程唤醒
     */
    private void awake(){
        //进程唤醒的主要工作是将进程由阻塞队列中摘下，修改进程状态为就绪，然后链入就绪队列
    }

    /**
     * 创建10个可执行文件
     */
    private void initExeFile(){

    }
    public static StringBuffer decompile(String compliedResult){
        /**
         *  8个bit，前三位为操作码
         *  000自加，001自减，后面全为0
         *  010为申请设备，00为A设备，01为B设备，10为C设备，后3位为使用时间
         *  011为赋值,后面为赋值数据
         *  100为end，后面全为0
         */

        String op = compliedResult.substring(0,2);
        switch (op){
            case "000":
            case "001":
                break;
            case "100":
                break;
            case "010":
                break;
            case "011":
                break;
        }
        return null;
    }
    public static void main(String[] args) {
        File file = new File("/Users/apple/Desktop/test.txt");
        ArrayList<String> contents = new ArrayList<>();
        type(file,contents);
        for(String each:contents){
            System.out.println(each);
            String compiledResult = null;
            matcher1 = format1.matcher(each);
            if(matcher1.matches()){
                String name = matcher1.group(1);
                String action = matcher1.group(2);
                int value = map.get(name);
                if(action.equals("++")){
                    value++;
                    compiledResult="00000000";
                    System.out.println("这是自加，编译结果为："+compiledResult);
                }else{
                    value--;
                    compiledResult="00100000";
                    System.out.println("这是自减，编译结果为："+compiledResult);
                }
                PC++;
                map.put(name,value);
                System.out.println(name+":"+value);

            }

            matcher2 = format2.matcher(each);
            if(matcher2.matches()){
                compiledResult="010";
                String deviceName = matcher2.group(1);
                switch (deviceName){
                    case "A":compiledResult+=00;
                    break;
                    case "B":compiledResult+=01;
                    break;
                    case "C":compiledResult+=10;
                    break;
                }
                int time = Integer.parseInt(matcher2.group(2));
                System.out.println(deviceName+":"+time);

                String result = Integer.toBinaryString(time);
                StringBuffer s = new StringBuffer();
                for(int i=result.length();i<=3;i++){
                    s.append("0");
                }
                s.append(result);
                result = s.toString();

                compiledResult+=result;
                System.out.println(result);
                System.out.println("这是设备申请，编译结果为："+compiledResult);
                PC++;
            }

            if(each.matches(format3)){
                compiledResult="10000000";
                PC++;
                System.out.println("这是结束，编译结果为："+compiledResult);
            }

            matcher4 = format4.matcher(each);
            if(matcher4.matches()){
                compiledResult = "011";
                String name = matcher4.group(1);
                int num = Integer.parseInt(matcher4.group(2));

                String result = Integer.toBinaryString(num);
                StringBuffer s = new StringBuffer();
                for(int i=result.length();i<5;i++){
                    s.append("0");
                }
                s.append(result);
                result = s.toString();

                compiledResult+=result;

                map.put(name,num);
                PC++;
                System.out.println("这是赋值，编译结果为："+compiledResult);
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

