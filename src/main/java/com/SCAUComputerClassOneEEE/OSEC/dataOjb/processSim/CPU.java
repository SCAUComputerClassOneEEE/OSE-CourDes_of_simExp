package com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim;

import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.controller.MySceneController;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.equipmentsSim.Equipment;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.dataService.impl.DiskSimService;
import com.SCAUComputerClassOneEEE.OSEC.utils.MainUI;
import com.SCAUComputerClassOneEEE.OSEC.utils.TaskThreadPools;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import lombok.SneakyThrows;

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

    //public static final ArrayList<PCB> blankQueue = new ArrayList<>();//空白队列
    public static final ObservableList<PCB> readyQueue = FXCollections.observableArrayList();//就绪队列
    public static final ObservableList<PCB> blockedQueue = FXCollections.observableArrayList();//阻塞队列

    //预先设置的10个可运行文件，形式仅仅是文件
    private ArrayList<AFile> exeFiles = new ArrayList<>();

    //数据服务层
    private DiskSimService diskSimService = new DiskSimService();

    private static PCB curPCB = null;//当前正在运行的进程的控制块

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
        initExeFile();//初始化10个可执行文件

        curPCB = processScheduling();
        //以下为cpu正式循环运行
        while (true){

            //中断处理区
            interruptHandling();

            randomPosses();

            showReadyAndBlockQueue();

            if (curPCB==null){
                if (readyQueue.size()>0){
                    curPCB = processScheduling();
                }
            }
            if (curPCB!=null){
                System.out.println("进程:"+curPCB.getProcessId()+"正在运行");
            }

            //程序运行区，一次运行一条指令
            System.out.println("\n----------指令执行-------------");
            psw = clock.timeRotation();
            System.out.println("----------指令执行-------------");

        }
    }

    /**
     * 随机产生进程申请
     */
    private void randomPosses(){
        System.out.println("\n----------随机产生进程-------------");
        if (readyQueue.size()+blockedQueue.size()>10){
            System.out.println("系统最多存在10个进程");
            return;
        }
        if ((int)(Math.random()*5)==4){
            AFile executeFile = exeFiles.get((int)(10*Math.random()));
            create(executeFile);//创建进程
            System.out.println("随机生成了新进程");
        }else {
            System.out.println("没有产生新进程");
        }
        System.out.println("----------随机产生进程-------------");
    }

    /**
     * 中断处理
     */
    private void interruptHandling(){
        System.out.println("\n----------处理中断-------------");
        System.out.println("正在检测中断···");
        System.out.println("程序状态字为:"+psw);
        //先处理程序结束中断
        if ((psw&CPU.EOP)!=0){//程序结束
            System.out.println("正在处理程序结束中断···");
            //输出X的最终结果
            Platform.runLater(()-> MainUI.mainUI.getFinalResult().setText("X="+AX));
            //调度
            curPCB = processScheduling();
            //去除程序结束中断与时间片结束中断
            psw = psw ^ CPU.EOP;
            if ((psw & CPU.TSE) != 0){
                psw = psw ^ CPU.TSE;
            }
        }

        //轮到设备中断，防止时间片到期还未发出设备申请
        if((psw&CPU.IOI)!=0){
            block(curPCB);
            //IO中断(请求设备)
            System.out.println("正在处理设备中断···");
            char equip = IR.charAt(1);
            int time = Integer.parseInt(IR.substring(2));
            //请求分配设备
            Equipment.getEquipment().distributeEQ(equip,curPCB,time);
            System.out.println("申请设备"+equip+":"+time+"秒");
            curPCB = processScheduling();
            psw = psw ^ CPU.IOI;
        }

        //时间片结束中断
        if ((psw&CPU.TSE)!=0){

            System.out.println("正在处理时间片结束中断···");

            if (curPCB != null){
                //保存X的值
                curPCB.setAX(AX);
                //添加回就绪队列
                readyQueue.add(curPCB);
            }
            //调度
            curPCB = processScheduling();

            psw = psw ^ CPU.TSE;
        }
        System.out.println("----------处理中断-------------");
    }

    /**
     * 进程调度
     */
    private PCB processScheduling(){

        System.out.println("\n----------调度-------------");
        System.out.println("正在执行调度算法···");
        clock.setTimeSlice(6);
        PCB newProcess = null;
        if (readyQueue.size()>0){
            newProcess = readyQueue.get(0);
            //恢复现场
            AX = newProcess.getAX();
            readyQueue.remove(newProcess);
        }

        if (newProcess==null){
            System.out.println("调度闲逛进程开始运行");
        }else {
            System.out.println("调度"+newProcess.getProcessId()+"号进程开始运行");
        }
        System.out.println("-----------调度------------");
        return newProcess;
    }

    /**
     * 一个cpu周期
     */
    public static int CPUCycles(){

        int result = 0;
        //如果当前无进程，闲逛，啥也不做
        if (curPCB==null){
            IR = "当前无进程";
            System.out.println("我在闲逛");
        }
        else {
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
                result = psw | CPU.IOI;
            }else if(IR.contains("=")){
                AX = Integer.parseInt(IR.substring(2));
                System.out.println("X赋值为"+AX);
            }
            else{
                //进程运行结束，销毁进程
                destroy(curPCB);
                System.out.println("进程:"+curPCB.getProcessId()+"的执行结果为:X="+AX);
                result = psw | CPU.EOP;
            }

        }

        return result;
    }

    /**进程控制原语
     * 进程申请
     * 参数为一个可执行文件对象
     */
    private static void create(AFile aFile){
        PCB newProcess = new PCB();//空白进程控制块
        //申请内存
        //System.out.println("可执行文件"+aFile.getAbsoluteLocation()+"的编码内容是:"+aFile.getDiskContent());
        int pointer;
        try {
            pointer = Memory.getMemory().malloc(aFile.getDiskContent().toCharArray());
        }catch (Exception e){
            return;
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
    public static void awake(PCB awakePCB){
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
                    diskSimService.write_exeFile(exeFiles.get(i*5+j), "X++;!A7;X++;X++;!B5;X++;!C3;X++;end;");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private static void showReadyAndBlockQueue(){
        System.out.println("\n-------------队列展示--------------");
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
        System.out.println("-------------队列展示--------------");    }

}

