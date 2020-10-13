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

    private static final ArrayList<PCB> blankQueue = new ArrayList<>();//空白队列
    private static final ArrayList<PCB> readyQueue = new ArrayList<>();//就绪队列
    private static final ArrayList<PCB> blockedQueue = new ArrayList<>();//阻塞队列

    //预先设置的10个可运行文件，形式仅仅是文件
    private ArrayList<AFile> exeFiles = new ArrayList<>();

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
        AFile executeFile = exeFiles.get((int)(10*Math.random()));
        create(executeFile);//创建进程
        create(executeFile);
        create(executeFile);
        curPCB = readyQueue.get(0);
        readyQueue.remove(curPCB);
        //以下为cpu正式循环运行
        while (true){

            //如果上一条为闲逛进程且当前就绪队列有进程
            if (curPCB==null&&readyQueue.size()>0){
                processScheduling();
            }
            System.out.println("psw=" + psw);
            //中断处理区
            interruptHandling();
            //程序运行区，一次运行一条指令
            psw = clock.timeRotation();

        }
    }

    /**
     * 中断处理
     */
    private void interruptHandling(){
        //先处理程序结束中断
        if ((psw&CPU.EOP)!=0){//程序结束
            //输出X的最终结果
            Platform.runLater(()-> MainUI.mainUI.getFinalResult().setText("X="+AX));
            //调度
            curPCB = processScheduling();
            //去除程序结束中断与时间片结束中断
            psw = psw | CPU.EOP;
        }

        //轮到设备中断，防止时间片到期还未发出设备申请
        if((psw&CPU.IOI)!=0){
            //IO中断(请求设备)
                /*
                得到请求的设备与请求的时间，向设备管理器申请设备
                 */
            psw = psw | CPU.IOI;
        }

        //时间片结束中断
        if ((psw&CPU.TSE)!=0){
            if (curPCB != null){
                //保存X的值
                curPCB.setAX(AX);
                //添加回就绪队列
                readyQueue.add(curPCB);
            }
            //调度
            curPCB = processScheduling();
        }

    }

    /**
     * 进程调度
     */
    private PCB processScheduling(){
        PCB newProcess = null;
        if (readyQueue.size()>0){
            newProcess = readyQueue.get(0);
            //恢复现场
            AX = newProcess.getAX();
            readyQueue.remove(newProcess);
        }
        return newProcess;
    }

    /**
     * 一个cpu周期
     */
    public static int CPUCycles(){
        //如果当前无进程，闲逛，啥也不做
        if (curPCB==null){
            IR = "当前无进程";
            System.out.println("我在闲逛");
            return 0;
        }

        //从内存中取出指令字符
        char ir = Memory.getMemory().getUserMemoryArea()[curPCB.getPointerToMemory()+curPCB.getPC()];
        //pc+1
        curPCB.setPC(curPCB.getPC()+1);
        //编译
        IR = Compile.decompile(ir);
        System.out.println("正在执行指令:"+IR);
        //执行
        if(IR.contains("++")){
            AX++;
            System.out.println("X的值为:"+AX);
        }else if(IR.contains("--")){
            AX--;
            System.out.println("X的值为:"+AX);
        }else if(IR.contains("!")){
            char equip = IR.charAt(1);
            int time = Integer.parseInt(IR.substring(2));
            System.out.println("申请设备"+equip+":"+time+"秒");
            return psw | CPU.IOI;
        }else if(IR.contains("=")){
            AX = Integer.parseInt(IR.substring(2));
            System.out.println("X赋值为"+AX);
        }
        else{
            destroy(curPCB);
            System.out.println("程序结束");
            return psw | CPU.EOP;
        }

        return 0;
    }

    /**进程控制原语
     * 进程申请
     * 参数为一个可执行文件对象
     */
    private static void create(AFile aFile){
        PCB newProcess = new PCB();//空白进程控制块
        //申请内存
        //System.out.println("可执行文件"+aFile.getAbsoluteLocation()+"的编码内容是:"+aFile.getDiskContent());
        int pointer = -1;
        try {
            pointer = Memory.getMemory().malloc(aFile.getDiskContent().toCharArray());
        }catch (Exception e){
            System.out.println(e.getMessage());
            //未分配到内存，进入空白pcb队列
            blankQueue.add(newProcess);
        }
        //System.out.println("进程分配到的内存首地址:"+pointer);
        //成功分配内存，开始填写PCB
        newProcess.setPointerToMemory(pointer);
        //添加进就绪队列并显示结果
        readyQueue.add(newProcess);
        //Platform.runLater(()-> MainUI.mainUI.getFinalResult().setText("进程创建成功！"));
    }

    /**进程控制原语
     * 进程销毁
     */
    private static void destroy(PCB destroyProcess){
        //回收内存空间
        try{
            Memory.getMemory().recovery(destroyProcess.getPointerToMemory());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        //回收PCB,运行时会把pcb从就绪队列中拿出来
        readyQueue.remove(destroyProcess);
        //显示结果
        //Platform.runLater(()-> MainUI.mainUI.getFinalResult().setText("进程删除成功！"));
    }

    /**进程控制原语
     * 进程阻塞
     */
    private static void block(PCB blockPCB){
        //保存运行进程的 CPU 现场 PC直接存在于PCB中，无需保存
        blockPCB.setAX(AX);
        //将进程链入对应的阻塞队列
        blockedQueue.add(blockPCB);
        //转向进程调度
    }


    /**进程控制原语
     * 进程唤醒
     */
    private static void awake(PCB awakePCB){
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
                exeFiles.add(diskSimService.createFile(Main.fileTree.getRootTree().getChildren().get(i).getValue(), String.valueOf(j), 16));
                try {
                    diskSimService.write_exeFile(exeFiles.get(i*5+j), "X++;X--;X=6;!A2;!B6;end;");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private static void showQueue(){
        System.out.print("就绪队列进程id:");
        for (PCB each:readyQueue){
            System.out.print(each.getProcessId()+" ");
        }
        System.out.println();

        System.out.print("阻塞队列进程id:");
        for (PCB each:blockedQueue){
            System.out.print(each.getProcessId()+" ");
        }
        System.out.println();
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

