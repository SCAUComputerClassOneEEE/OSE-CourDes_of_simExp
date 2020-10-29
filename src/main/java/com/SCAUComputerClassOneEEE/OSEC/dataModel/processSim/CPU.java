package com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim;

import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.controller.MySceneController;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.dataService.DeviceSimService;
import com.SCAUComputerClassOneEEE.OSEC.dataService.DiskSimService;
import com.SCAUComputerClassOneEEE.OSEC.dataService.ProcessSimService;
import com.SCAUComputerClassOneEEE.OSEC.utils.TaskThreadPools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;

/**
 * @author hlf
 * @date 25/8/2020
 */
public class CPU implements Runnable{

    public boolean WAITING = false;
    public static int EOP = 1;//程序结束
    public static int TSE = 1 << 1;//时间片结束
    public static int IOI = 1 << 2;//IO中断发生

    private static CPU cpu = new CPU();
    public static String IR;
    public static int PC = 0;

    public static int psw = 0;//程序状态字
    @Getter
    private static int AX =0;

    public static final ArrayList<PCB> allPCB = new ArrayList<>();
    public static final ObservableList<PCB> readyQueue = FXCollections.observableArrayList();//就绪队列
    public static final ObservableList<PCB> blockedQueue = FXCollections.observableArrayList();//阻塞队列

    //预先设置的10个可运行文件，形式仅仅是文件
    private ArrayList<AFile> exeFiles = new ArrayList<>();

    //数据服务层
    private DiskSimService diskSimService = new DiskSimService();

    public static PCB curPCB = null;//当前正在运行的进程的控制块

    private final Clock clock = Clock.getClock();

    public static CPU getCpu(){
        return cpu;
    }

    @SneakyThrows
    @Override
    public void run() {
        char [] osCode = new char[50];
        try {
            PCB os = new PCB(0,50, ProcessSimService.getColors().get(0),0,"os");
             ProcessSimService.getColors().remove(0);
            allPCB.add(os);
            Memory.getMemory().malloc(osCode);
        }catch (Exception e){
            e.printStackTrace();
        }
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
        synchronized (this) {
            while (true){
                if (WAITING) {
                    waitCPU();
                }

                //中断处理区
                interruptHandling();

                //随机产生新进程
                randomPosses();


                //showReadyAndBlockQueue();

                //判断是否需要调度
                if (curPCB==null){
                    if (readyQueue.size()>0){
                        curPCB = processScheduling();
                    }
                }
                if (curPCB!=null){
                    //System.out.println("进程:"+curPCB.getProcessId()+"正在运行");
                    MySceneController.runningPCBIDSim.setValue(String.valueOf(curPCB.getProcessId()));
                }
                else {
                    MySceneController.runningPCBIDSim.setValue("当前进程为闲逛进程");
                }

                //程序运行区，一次运行一条指令
                //System.out.println("\n----------指令执行-------------");
                psw = clock.timeRotation();
                //System.out.println("----------指令执行-------------");

            }
        }
    }

    public void waitCPU() throws InterruptedException {
        this.wait();
    }

    public void notifyCPU() {
        synchronized (this) {
            this.notify();
        }
    }


    /**
     * 一个cpu周期
     */
    public static int CPUCycles() throws InterruptedException {
        //当前cpu周期结束后返回的结果
        int result = 0;
        //当前进程为闲逛进程
        if (curPCB==null){
            MySceneController.runningIRSim.setValue("闲逛进程");
            IR = "闲逛中";
            //System.out.println("我在闲逛");
        }
        //不为闲逛进程
        else {
            //PC指向下一条指令在内存中的地址下标，取出指令
            char nextInstructionCode = Memory.getMemory().getUserMemoryArea()[PC++];

            //将取出的指令编码进行反编译，得出指令的文本表达
            IR = Compile.decompile(nextInstructionCode);

            //更新界面
            MySceneController.runningIRSim.setValue(IR);

            MySceneController.intermediateResultSim.setValue("");
            Thread.sleep(300);
            //System.out.println("正在执行指令:"+IR);

            //执行
            if(IR.contains("++")){
                AX++;
                //System.out.println("X的值为:"+AX);
                MySceneController.intermediateResultSim.setValue("X+1的值为:"+AX);
            }else if(IR.contains("--")){
                AX--;
                //System.out.println("X的值为:"+AX);
                MySceneController.intermediateResultSim.setValue("X-1的值为:"+AX);
            }else if(IR.contains("!")){
                char equip = IR.charAt(1);
                int time = Integer.parseInt(IR.substring(2));
                MySceneController.intermediateResultSim.setValue("申请设备"+equip+":"+time+"秒");
                result = psw | CPU.IOI;
            }else if(IR.contains("=")){
                AX = Integer.parseInt(IR.substring(2));
                //System.out.println("X赋值为"+AX);
                MySceneController.intermediateResultSim.setValue("X赋值为"+AX);
            }
            else{
                //进程运行结束，销毁进程
                 ProcessSimService.getProcessSimService().destroy(curPCB);
                //System.out.println("进程"+curPCB.getProcessId()+"执行结果为:X="+AX);
                MySceneController.finalResultSim.setValue("进程"+curPCB.getProcessId()+"执行结果为X="+AX);
                result = psw | CPU.EOP;
            }

        }
        return result;
    }


    /**
     * 创建10个可执行文件
     */
    public void initExeFile(){
        diskSimService.createFile(Main.fileTree.getRootTree().getValue(), "ef1", 8);
        diskSimService.createFile(Main.fileTree.getRootTree().getValue(), "ef2", 8);
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 5; j++) {
                exeFiles.add(diskSimService.createFile(Main.fileTree.getRootTree().getChildren().get(i).getValue(), String.valueOf("e"+j), 16));
                try {
                    diskSimService.write_exeFile(exeFiles.get(i*5+j), "X=0;!A6;X++;X++;X++;X++;X++;X++;X++;X++;X++;end;");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    /**
     * 随机产生进程申请
     */
    private void randomPosses(){
        //System.out.println("\n----------随机产生进程-------------");
        if ((int)(Math.random()*6)==5){
            AFile executeFile = exeFiles.get((int)(exeFiles.size()*Math.random()));
             ProcessSimService.getProcessSimService().create(executeFile);//创建进程
            Memory.getMemory().MAT_display();
            //System.out.println("随机生成了新进程");
        }else {
            //System.out.println("没有产生新进程");
        }
        //System.out.println("----------随机产生进程-------------");
    }

    /**
     * 中断处理
     */
    private void interruptHandling() throws InterruptedException {
        //System.out.println("\n----------处理中断-------------");
        //System.out.println("正在检测中断···");
        //System.out.println("程序状态字为:"+psw);
        //先处理程序结束中断
        if ((psw&CPU.EOP)!=0){//程序结束
            //System.out.println("正在处理程序结束中断···");
            MySceneController.intermediateResultSim.setValue("处理程序结束中断···");

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
             ProcessSimService.getProcessSimService().block(curPCB);
            //IO中断(请求设备)
            //System.out.println("正在处理设备中断···");
            MySceneController.intermediateResultSim.setValue("处理设备中断···");
            char equip = IR.charAt(1);
            int time = Integer.parseInt(IR.substring(2));
            //请求分配设备
            DeviceSimService.getDeviceSimService().distributeDevice(equip,curPCB,time);
            //System.out.println("申请设备"+equip+":"+time+"秒");
            curPCB = processScheduling();
            psw = psw ^ CPU.IOI;
        }

        //时间片结束中断
        if ((psw&CPU.TSE)!=0){

            //System.out.println("正在处理时间片结束中断···");
            MySceneController.intermediateResultSim.setValue("处理时间片结束中断···");

            if (curPCB != null){
                //保存X的值
                curPCB.setAX(AX);
                curPCB.setNextInstruction(CPU.PC-curPCB.getPointerToMemory());
                curPCB.setRemainInstructions(curPCB.getTotalSize()-curPCB.getNextInstruction());
                curPCB.setProgressRate(((double)curPCB.getTotalSize()-curPCB.getRemainInstructions())/curPCB.getTotalSize());
                //添加回就绪队列
                readyQueue.add(curPCB);
            }
            //调度
            curPCB = processScheduling();

            psw = psw ^ CPU.TSE;
        }
        //System.out.println("----------处理中断-------------");
    }


    /**
     * 进程调度
     */
    private PCB processScheduling(){
        //System.out.println("\n----------调度-------------");
        //System.out.println("正在执行调度算法···");
        //重置时间片
        clock.setTimeSlice(6);
        PCB nextProcess = null;
        if (readyQueue.size() > 0){
            //从就绪队列中摘取出来
            nextProcess = readyQueue.get(0);
            readyQueue.remove(nextProcess);
            //恢复现场
            AX = nextProcess.getAX();
            //即将被调度的进程的首地址+该进程下一条要执行的指令
            PC = nextProcess.getPointerToMemory() + nextProcess.getNextInstruction();
        }

        /*if (nextProcess==null){
            System.out.println("调度闲逛进程开始运行");
        }else {
            System.out.println("调度"+nextProcess.getProcessId()+"号进程开始运行");
        }*/
        //System.out.println("-----------调度------------");
        return nextProcess;
    }

    /**
     * 进程在内存的时间+1
     */
    public void timeAdd(){
        for(int i = 1; i < allPCB.size(); i++){
            PCB each = allPCB.get(i);
            each.totalTimeProperty().setValue(each.getTotalTime()+1);

        }
    }
/*
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
        System.out.println("-------------队列展示--------------");
    }*/
}

