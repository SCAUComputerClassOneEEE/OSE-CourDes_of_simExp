package com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim;

import com.SCAUComputerClassOneEEE.OSEC.controller.MainSceneController;
import com.SCAUComputerClassOneEEE.OSEC.utils.OS;
import lombok.Data;

import java.util.concurrent.ExecutionException;

/**
 * @author hlf
 * @date 25/8/2020
 */
@Data
public class Clock{

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
        OS.deviceSimService.decTime();
        OS.cpu.timeAdd();
        //返回中断字
        int psw = CPU.instructionCycle();
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
            return CPU.TSE | psw;
        }

        //System.out.println("一条代码结束执行，用时： " + (end2-sTime));
        return psw;
    }

    public void reset() {
        cpuRanTime = 0;
    }
}
