package com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim;

import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.dataService.impl.DiskSimService;
import com.SCAUComputerClassOneEEE.OSEC.utils.MainUI;
import com.SCAUComputerClassOneEEE.OSEC.utils.TaskThreadPools;
import javafx.application.Platform;
import lombok.SneakyThrows;

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
public class CPU implements Runnable{

    public static int EOP = 1;//程序结束
    public static int TSE = 1 << 1;//时间片结束
    public static int IOI = 1 << 2;//IO中断发生

    private static CPU cpu = new CPU();
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

    public static int psw = 0;//程序状态字
    private static int AX =0;

    private ArrayList<PCB> blankQueue = new ArrayList<>();//空白队列
    private ArrayList<PCB> readyQueue = new ArrayList<>();//就绪队列
    private ArrayList<PCB> blockedQueue = new ArrayList<>();//阻塞队列

    //预先设置的10个可运行文件，形式仅仅是文件
    private ArrayList<AFile> exeFile = new ArrayList<>();

    //数据服务层
    private DiskSimService diskSimService = new DiskSimService();

    private static PCB curPCB;//当前正在运行的进程的控制块

    private final Clock clock = Clock.getClock();

    public static CPU getCpu(){
        return cpu;
    }

    @SneakyThrows
    @Override
    public void run() {
        TaskThreadPools.execute(clock);
        cpu();
    }
    /**
     * cpu
     */
    public void cpu() throws Exception {
        initExeFile();//初始化10个可执行文件先
        AFile executeFile = exeFile.get((int)(10*Math.random()));
        create(executeFile);//创建进程
        curPCB = readyQueue.get(0);
        //以下为cpu正式循环运行
        while (true){ //还没加入多线程，会卡住主界面
            //先处理中断
            if ((psw&CPU.EOP)!=0){
                //程序结束
            }
            if ((psw&CPU.TSE)!=0){
                //时间片结束,调用轮转调度算法
            }
            if((psw&CPU.IOI)!=0){
                //IO中断(请求设备)
            }
            //程序运行区，一次运行一条指令
            //执行指令，需要配合时钟
            psw = clock.timeRotation();

        }
    }


    /**
     * 进程调度
     */
    private void processScheduling() throws Exception {

    }

    /**
     * 给乐烽做，要求:识别IR中的指令，写出分支语句，分支语句的具体功能可以留给ky写
     */
    public static int CPUCycles(){
        //从内存中取出指令字符
        char ir = Memory.getMemory().getUserMemoryArea()[curPCB.getPointerToMemory()+curPCB.getPC()];
        //pc+1
        curPCB.setPC(curPCB.getPC()+1);
        //编译
        IR = Compile.decompile(ir);
        System.out.println("当前指令:"+IR);
        if(IR.contains("++")){
            System.out.println(++AX);
        }else if(IR.contains("--")){
            System.out.println(--AX);
        }else if(IR.contains("!")){
/*            String equipment = matcher2.group(1);
            String time = matcher2.group(2);*/
            System.out.println("设备");
        }else if(IR.contains("=")){
//            String num = matcher4.group(2);
            System.out.println("赋值");
        }
        else{
            //end
            System.out.println("结束啦");
        }

        return 0;
    }

    /**进程控制原语
     * 进程申请
     */
    private void create(AFile aFile) throws Exception {
        PCB newProcess = new PCB();//空白进程控制块
        //申请内存
        System.out.println("可执行文件"+aFile.getAbsoluteLocation()+"的编码内容是:"+aFile.getDiskContent());
        int pointer = Memory.getMemory().malloc(aFile.getDiskContent().toCharArray());
        System.out.println("进程分配到的内存首地址:"+pointer);
        //填写PCB
        newProcess.setPointerToMemory(pointer);
        newProcess.setPC(0);
        //添加进就绪队列并显示结果
        readyQueue.add(newProcess);
        Platform.runLater(()-> MainUI.mainUI.getFinalResult().setText("进程创建成功！"));

    }

    /**进程控制原语
     * 进程销毁
     */
    private void destroy(PCB pcb) throws Exception {
        //回收内存空间
        Memory.getMemory().recovery(pcb.getPointerToMemory());
        //回收PCB,运行时会把pcb从就绪队列中拿出来
        //pcb.free
        //显示结果
        MainUI.mainUI.getFinalResult().setText("进程删除成功！");
    }

    /**进程控制原语
     * 进程阻塞
     */
    private void block(PCB blockPCB){
        //保存运行进程的 CPU 现场
        blockPCB.setPC(PC);
        blockPCB.setAX(AX);
        //修改进程状态
        //blockPCB.setProcessState();
        //将进程链入对应的阻塞队列
        blockedQueue.add(blockPCB);
        //转向进程调度
    }


    /**进程控制原语
     * 进程唤醒
     */
    private void awake(PCB awakePCB){
        //进程唤醒的主要工作是将进程由阻塞队列中摘下，修改进程状态为就绪，然后链入就绪队列
        blockedQueue.remove(awakePCB);
        readyQueue.add(awakePCB);
    }

    /**
     * 创建10个可执行文件
     */
    public void initExeFile(){
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

