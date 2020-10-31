package com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim;

import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.OS;
import com.SCAUComputerClassOneEEE.OSEC.controller.MySceneController;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.dataService.DeviceSimService;
import com.SCAUComputerClassOneEEE.OSEC.dataService.DiskSimService;
import com.SCAUComputerClassOneEEE.OSEC.dataService.ProcessSimService;
import com.SCAUComputerClassOneEEE.OSEC.utils.TaskThreadPools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.SneakyThrows;

import java.util.ArrayList;

/**
 * @author hlf & sky
 * @date 25/8/2020
 */
public class CPU implements Runnable{

    private static final CPU CPU = new CPU();// cpu单例

    public boolean WAITING = false;// cpu进程暂停状态

    public static String IR = "闲逛进程";// 指令寄存器IR
    public static int PC = 0;// 程序计数器PC
    public static int psw = 0;// 程序状态字
    public static int AX =0;// 数据寄存器AX
    public static PCB curPCB = null;// 当前正在运行的进程的控制块

    // 三种中断
    public static int EOP = 1;// 程序结束
    public static int TSE = 1 << 1;// 时间片结束
    public static int IOI = 1 << 2;// IO中断发生

    // 磁盘数据服务层
    private static final DiskSimService diskSimService = new DiskSimService();

    // 预先设置的10个可运行文件，形式仅仅是文件
    public static final ArrayList<AFile> exeFiles = new ArrayList<>();

    public static final ArrayList<PCB> allPCB = new ArrayList<>();// 所有进程的PCB链表
    public static final ObservableList<PCB> readyQueue = FXCollections.observableArrayList();// 就绪进程PCB队列
    public static final ObservableList<PCB> blockedQueue = FXCollections.observableArrayList();// 阻塞进程PCB队列

    @SneakyThrows
    @Override
    public void run() {
        executeCPU();
    }

    /**
     * 执行
     * @throws Exception
     */
    private void executeCPU() throws Exception {
        //创建可执行文件
        //initExeFile();
        cpu();
    }


    /**
     * cpu
     */
    public void cpu() throws Exception {
        // cpu循环运行
        synchronized (this) {
            while (true) {
                // 暂停按键是否被按下
                if (WAITING) { waitCpu(); }
                // 中断处理
                interruptHandling();
                // 随机产生新进程
                //randomPosses();
                // 判断是否需要调度
                if (curPCB == null) {
                    // 当前为闲逛进程 检查就绪队列有无新进程加入
                    if (readyQueue.size() > 0) {
                        curPCB = processScheduling();
                    }
                }
                if (curPCB != null) {
                    MySceneController.runningPCBIDSim.setValue(String.valueOf(curPCB.getProcessId()));
                }
                else {
                    MySceneController.runningPCBIDSim.setValue("当前进程为闲逛进程");
                }
                // timeRotation中运行一个指令周期
                psw = OS.clock.timeRotation();
            }
        }
    }

    /**
     * 一个指令周期
     */
    public static int instructionCycle() throws InterruptedException {
        // 当前指令周期结束后返回的结果
        int result = 0;
        // 当前进程为闲逛进程
        if (curPCB == null) {
            MySceneController.runningIRSim.setValue("闲逛进程");
            IR = "闲逛中···";
            return result;
        }
        // 不为闲逛进程
        else {
            // 取出指令，PC指向下一条指令在内存中的地址下标
            char nextInstructionCode = Memory.getMemory().getUserMemoryArea()[PC++];
            // 将取出的指令编码进行反编译，得出指令的文本表达
            IR = Compile.decompile(nextInstructionCode);
            // 更新界面
            MySceneController.runningIRSim.setValue(IR);
            MySceneController.intermediateResultSim.setValue("");
            // 模拟停顿效果
            Thread.sleep(300);

            // 执行
            if(IR.contains("++")) {
                // 自增
                AX++;
                MySceneController.intermediateResultSim.setValue("X+1的值为:" + AX);
            }else if(IR.contains("--")) {
                // 自减
                AX--;
                MySceneController.intermediateResultSim.setValue("X-1的值为:" + AX);
            }else if(IR.contains("!")) {
                // 申请设备
                char equip = IR.charAt(1);
                int time = Integer.parseInt(IR.substring(2));
                MySceneController.intermediateResultSim.setValue("申请设备" + equip + ":" + time + "秒");
                result = psw | IOI;
            }else if(IR.contains("=")) {
                // 赋值
                AX = Integer.parseInt(IR.substring(2));
                MySceneController.intermediateResultSim.setValue("X赋值为" + AX);
            }
            else {// 结束
                ProcessSimService.getProcessSimService().destroy(curPCB);
                MySceneController.finalResultSim.setValue("进程" + curPCB.getProcessId() + "执行结果为X=" + AX);
                result = psw | EOP;
            }

        }
        return result;
    }

    /**
     * 随机产生进程申请
     */
    private void randomPosses(){
        if ((int)(Math.random() * 6) == 5) {
            AFile executeFile = exeFiles.get((int)(exeFiles.size() * Math.random()));
            // 创建进程
            ProcessSimService.getProcessSimService().create(executeFile);
        }
    }
/*
    *//**
     * 创建10个可执行文件
     *//*
    public void initExeFile(){
        diskSimService.createFile(OS.fileTree.getRootTree().getValue(), "ef1", 8);
        diskSimService.createFile(OS.fileTree.getRootTree().getValue(), "ef2", 8);

        String[] exeFileContents = {
                "X=0;!A6;X++;X++;!C3;X++;X--;end;",
                "X=4;!B5;X++;X++;!A4;X++;X++;X++;X++;X++;X++;end;",
                "X=2;X--;!A5;X--;!C2;X++;!B3;X++;X++;X++;X++;X++;X++;end;",
                "X=0;!A2;X++;!B2;X++;!C2;X++;X++;X++;X++;end;",
                "X=5;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;X++;end"};
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                exeFiles.add(diskSimService.createFile(OS.fileTree.getRootTree().getChildren().get(i).getValue(), "e" + j, 16));
                try {
                    diskSimService.write_exeFile(exeFiles.get(i*5+j), exeFileContents[(int)(Math.random() * 4)]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    /**
     * 中断处理
     */
    private void interruptHandling() throws InterruptedException {
        // 处理程序结束中断
        if ((psw& EOP) != 0) {
            MySceneController.intermediateResultSim.setValue("处理程序结束中断···");
            // 调度
            curPCB = processScheduling();
            // 去除程序结束中断
            psw = psw ^ EOP;
            // 程序已经结束，如果此时也有时间片中断，则去除时间片结束中断
            if ((psw & TSE) != 0) {
                psw = psw ^ TSE;
            }
        }

        // 设备中断，防止时间片到期还未发出设备申请
        if((psw & IOI) != 0) {
            ProcessSimService.getProcessSimService().block(curPCB);
            MySceneController.intermediateResultSim.setValue("处理设备中断···");
            // 获取指令中的申请信息
            char equip = IR.charAt(1);
            int time = Integer.parseInt(IR.substring(2));
            // 请求分配设备
            DeviceSimService.getDeviceSimService().distributeDevice(equip,curPCB,time);
            // 调度
            curPCB = processScheduling();
            // 去除设备中断
            psw = psw ^ IOI;
        }
        // 时间片结束中断
        if ((psw & TSE) != 0) {
            if (curPCB != null) {
                MySceneController.intermediateResultSim.setValue("处理时间片结束中断···");
                // 保存X的值
                curPCB.setAX(AX);
                curPCB.setNextInstruction(PC - curPCB.getPointerToMemory());
                curPCB.setRemainInstructions(curPCB.getTotalSize() - curPCB.getNextInstruction());
                curPCB.setProgressRate(((double)curPCB.getTotalSize() - curPCB.getRemainInstructions()) / curPCB.getTotalSize());
                // 添加回就绪队列
                readyQueue.add(curPCB);
            }
            // 调度
            curPCB = processScheduling();
            // 去除时间片中断
            psw = psw ^ TSE;
        }
        MySceneController.intermediateResultSim.setValue("");
    }

    /**
     * 进程调度
     */
    private PCB processScheduling() {
        // 重置时间片
        OS.clock.setTimeSlice(6);
        PCB nextProcess = null;
        if (readyQueue.size() > 0) {
            // 从就绪队列中摘取出来
            nextProcess = readyQueue.get(0);
            readyQueue.remove(nextProcess);
            // 恢复现场
            AX = nextProcess.getAX();
            // 即将被调度的进程的首地址+该进程下一条要执行的指令
            PC = nextProcess.getPointerToMemory() + nextProcess.getNextInstruction();
        }
        return nextProcess;
    }

    /**
     * 进程在内存的时间+1
     */
    public void timeAdd() {
        for(int i = 1; i < allPCB.size(); i++){
            PCB each = allPCB.get(i);
            each.totalTimeProperty().setValue(each.getTotalTime()+1);
        }
    }

    /**
     * 阻塞cpu线程
     * @throws InterruptedException
     */
    public void waitCpu() throws InterruptedException {
        this.wait();
    }

    /**
     * 唤醒cpu线程
     */
    public void notifyCpu() {
        synchronized (this) {
            this.notify();
        }
    }

    public static CPU getCpu(){
        return CPU;
    }

    public void reset() {
        WAITING = false;
        IR = "闲逛进程";
        PC = 0;
        psw = 0;
        AX =0;
        curPCB = null;
        readyQueue.clear();
        blockedQueue.clear();
        int size = allPCB.size();
        for(int i = 1; i < size; i++) {
            allPCB.remove(1);
        }
    }
}

