package com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim;

import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataService.impl.DiskSimService;

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

    private int psw=0;//程序状态字

    private ArrayList<PCB> blankQueue = new ArrayList<>();//空白队列
    private ArrayList<PCB> readyQueue = new ArrayList<>();//就绪队列
    private ArrayList<PCB> blockedQueue = new ArrayList<>();//阻塞队列

    //预先设置的10个可运行文件，形式仅仅是文件
    private ArrayList<AFile> exeFile = new ArrayList<>();

    //数据服务层
    private DiskSimService diskSimService = new DiskSimService();

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
        diskSimService.createFile(Main.fileTree.getRootTree().getValue(), "a", 8);
        diskSimService.createFile(Main.fileTree.getRootTree().getValue(), "b", 8);
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 5; j++) {
                exeFile.add(diskSimService.createFile(Main.fileTree.getRootTree().getChildren().get(i).getValue(), String.valueOf(j), 16));
                try {
                    diskSimService.write_exeFile(exeFile.get(i*5+j), "X++;X--;X=6;!A2;!B6;end;");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }
    public void init(){
        initExeFile();
    }
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

