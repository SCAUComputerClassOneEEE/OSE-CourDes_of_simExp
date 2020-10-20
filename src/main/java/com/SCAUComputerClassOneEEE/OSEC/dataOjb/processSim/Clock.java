package com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim;

import com.SCAUComputerClassOneEEE.OSEC.controller.MySceneController;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.equipmentsSim.Equipment;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author hlf
 * @date 25/8/2020
 */
@Data
public class Clock implements Runnable{

    private static long cpuRanTime = 0;
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

    @Override
    public void run() {

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
        //System.out.println("========timeRotation=======");
        //System.out.println("一条代码开始执行");
        //时间片减一
        //System.out.println("剩余时间片：" + timeSlice);

        //
        MySceneController.cpuTimeSim.set(cpuRanTime++);
        MySceneController.timeSliceSim.setValue(timeSlice--);
        Equipment.decTime();
        //返回中断字
        int psw = CPU.CPUCycles();
        long end1 = System.currentTimeMillis();
        //补足1000ms时间
        Thread.sleep(999 - end1 + sTime);


        if (timeSlice == 0){
            //System.out.println("##时间片结束");
            timeSlice = 6;
            return CPU.TSE | psw;
        }

        //System.out.println("一条代码结束执行，用时： " + (end2-sTime));
        return psw;
    }
}
