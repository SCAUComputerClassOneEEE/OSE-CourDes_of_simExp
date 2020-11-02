package com.SCAUComputerClassOneEEE.OSEC.data_model.processSim;

import com.SCAUComputerClassOneEEE.OSEC.data_model.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.utils.CompileUtil;
import com.SCAUComputerClassOneEEE.OSEC.data_center.OSDataCenter;
import com.SCAUComputerClassOneEEE.OSEC.controller.MainSceneController;
import com.SCAUComputerClassOneEEE.OSEC.data_model.diskSim.AFile;
import com.SCAUComputerClassOneEEE.OSEC.data_model.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.data_service.ProcessSimService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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

/*    // 磁盘数据服务层
    private static final DiskSimService diskSimService = new DiskSimService();*/

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
        System.out.println(OSDataCenter.disk.exeFiles.size());
        if (OSDataCenter.disk.exeFiles.size() == 0) {
            initExeFile();
        }
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
                randomPosses();
                // 判断是否需要调度
                if (curPCB == null) {
                    // 当前为闲逛进程 检查就绪队列有无新进程加入
                    if (readyQueue.size() > 0) {
                        curPCB = processScheduling();
                    }
                }
                if (curPCB != null) {
                    MainSceneController.runningPCBIDSim.setValue(String.valueOf(curPCB.getProcessId()));
                }
                else {
                    MainSceneController.runningPCBIDSim.setValue("当前进程为闲逛进程");
                }
                // timeRotation中运行一个指令周期
                psw = OSDataCenter.clock.timeRotation();
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
            MainSceneController.runningIRSim.setValue("闲逛进程");
            IR = "闲逛中···";
            return result;
        }
        // 不为闲逛进程
        else {
            // 取出指令，PC指向下一条指令在内存中的地址下标
            char nextInstructionCode = Memory.getMemory().getUserMemoryArea()[PC++];
            // 将取出的指令编码进行反编译，得出指令的文本表达
            IR = CompileUtil.decompile(nextInstructionCode);
            // 更新界面
            MainSceneController.runningIRSim.setValue(IR);
            MainSceneController.intermediateResultSim.setValue("");
            // 模拟停顿效果
            try{
                Thread.sleep(300);
            }
            catch (Exception ignored){

            }

            // 执行
            if(IR.contains("++")) {
                // 自增
                AX++;
                MainSceneController.intermediateResultSim.setValue("X+1的值为:" + AX);
            }else if(IR.contains("--")) {
                // 自减
                AX--;
                MainSceneController.intermediateResultSim.setValue("X-1的值为:" + AX);
            }else if(IR.contains("!")) {
                // 申请设备
                char equip = IR.charAt(1);
                int time = Integer.parseInt(IR.substring(2));
                MainSceneController.intermediateResultSim.setValue("申请设备" + equip + ":" + time + "秒");
                result = psw | IOI;
            }else if(IR.contains("=")) {
                // 赋值
                AX = Integer.parseInt(IR.substring(2));
                MainSceneController.intermediateResultSim.setValue("X赋值为" + AX);
            }
            else {// 结束
                ProcessSimService.getProcessSimService().destroy(curPCB);
                MainSceneController.finalResultSim.setValue("进程" + curPCB.getProcessId() + "执行结果为X=" + AX);
                result = psw | EOP;
            }

        }
        return result;
    }

    /**
     * 随机产生进程申请
     */
    private void randomPosses(){
        if (OSDataCenter.disk.exeFiles.size() == 0) return;
        //if ((int)(Math.random()) == 0) {
        if ((int)(Math.random()*6) == 5) {
            AFile executeFile = OSDataCenter.disk.exeFiles.get((int)(OSDataCenter.disk.exeFiles.size() * Math.random()));
            // 创建进程
            OSDataCenter.processSimService.create(executeFile);
        }
    }

    /**
     * 创建10个可执行文件
     */
    public void initExeFile(){
        OSDataCenter.diskSimService.createFile(OSDataCenter.fileTree.getRootTree().getValue(), "ef1", 8);
        OSDataCenter.diskSimService.createFile(OSDataCenter.fileTree.getRootTree().getValue(), "ef2", 8);

        // 可执行文件池
        String[] exeFileContents = {
                "X=0;\n!A6;\nX++;\nX++;\n!C3;\nX++;\nX--;\nend;",
                "X=4;\n!B5;\nX++;\nX++;\n!A4;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nend;",
                "X=2;\nX--;\n!A5;\nX--;\n!C2;\nX++;\n!B3;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nend;",
                "X=0;\n!A2;\nX++;\n!B2;\nX++;\n!C2;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nend;",
                "X=5;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nX++;\nend;"};

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                AFile newFile = OSDataCenter.diskSimService.createFile(OSDataCenter.fileTree.getRootTree().getChildren().get(i).getValue(), "e" + j, 16);
                try {
                    // 随机从文件池中选择
                    OSDataCenter.diskSimService.write_exeFile(newFile, exeFileContents[(int)(Math.random() * 5)]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 中断处理
     */
    private void interruptHandling() {
        // 处理程序结束中断
        if ((psw& EOP) != 0) {
            MainSceneController.intermediateResultSim.setValue("处理程序结束中断···");
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
            MainSceneController.intermediateResultSim.setValue("处理设备中断···");
            // 获取指令中的申请信息
            char equip = IR.charAt(1);
            int time = Integer.parseInt(IR.substring(2));
            // 请求分配设备
            OSDataCenter.deviceSimService.distributeDevice(equip,curPCB,time);
            // 调度
            curPCB = processScheduling();
            // 去除设备中断
            psw = psw ^ IOI;
        }
        // 时间片结束中断
        if ((psw & TSE) != 0) {
            if (curPCB != null) {
                MainSceneController.intermediateResultSim.setValue("处理时间片结束中断···");
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
        MainSceneController.intermediateResultSim.setValue("");
    }

    /**
     * 进程调度
     */
    private PCB processScheduling() {
        // 重置时间片
        OSDataCenter.clock.setTimeSlice(6);
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
        allPCB.clear();
    }

    /**
     * @author best lu
     * @date 25/8/2020
     */
    @Data
    public static class Clock{

        private static int TIME_UNIT = 999;
        private static int cpuRanTime = 0;
        private volatile static int timeSlice = 6;
        private static final Clock clock = new Clock();

        public void setTimeSlice(int timeSlice){
            Clock.timeSlice = timeSlice;
        }

        public long getCpuRanTime(){
            return cpuRanTime;
        }

        private Clock(){

        }

        public static Clock getClock(){
            return clock;
        }


        /**
         * 时间片轮转。
         * 分发一个时间单位（1000ms），去调用cpu的executeAndFetch方法，取指令（更新IR）、执行指令(取IR)。
         * @return 一个时间片内执行返回程序中断字
         * @throws InterruptedException
         * @throws ExecutionException
         */
        public synchronized int timeRotation() throws InterruptedException{

            long sTime = System.currentTimeMillis();

            MainSceneController.cpuTimeSim.set(cpuRanTime++);
            MainSceneController.timeSliceSim.setValue(timeSlice--);
            OSDataCenter.deviceSimService.decTime();
            OSDataCenter.cpu.timeAdd();
            //返回中断字
            int psw = instructionCycle();
            long end1 = System.currentTimeMillis();
            //补足1000ms时间
            try {
                Thread.sleep(TIME_UNIT - end1 + sTime);
            }
            catch (Exception ignored){

            }

            if (timeSlice == 0){
                //System.out.println("##时间片结束");
                timeSlice = 6;
                return TSE | psw;
            }

            //System.out.println("一条代码结束执行，用时： " + (end2-sTime));
            return psw;
        }

        public void reset() {
            cpuRanTime = 0;
        }
    }
}

