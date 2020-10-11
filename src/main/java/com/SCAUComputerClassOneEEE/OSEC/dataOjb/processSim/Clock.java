package com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim;

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

    @Setter
    @Getter
    private static boolean isExecuting = false;
    private Clock(){

    }

    public static Clock getClock(){
        return clock;
    }

    @SneakyThrows
    @Override
    public void run() {
        while(true){

            Thread.sleep(900);
//            System.out.println("------clock monitor---");
            if (isExecuting()){
                synchronized (clock){
                    if (isExecuting()){
                        setExecuting(false);
                        timeSlice --;
//                        System.out.println("时间片剩余，" + timeSlice);
                        if (timeSlice <= 0){
//                            System.out.println("##时间片结束");
                            timeSlice = 6;
                        }
                    }
                }
            }
//            System.out.println("------end   monitor---");
        }
    }

    public synchronized int timeRotation() throws InterruptedException, ExecutionException {
        synchronized (clock){
//            System.out.println("========timeRotation=======");
//            System.out.println("一条代码开始执行");
            setExecuting(true);
        }
        long sTime = System.currentTimeMillis();
        int psw = 0;
        FutureTask<Integer> execution = new FutureTask<>(CPU::execute);
        Thread cpuExecuteThread = new Thread(execution);
        cpuExecuteThread.start();

        //指令返回是否程序中断
        int executeRes = execution.get();
        /*
        据executeRes和timeSlice修改psw返回值
        当timeSlice = 0时，表示时间片结束
         */

        long end1 = System.currentTimeMillis();
        Thread.sleep(999 - end1 + sTime);


        long end2 = System.currentTimeMillis();

//        System.out.println("一条代码结束执行，用时： " + (end2-sTime));
        return psw;
    }
}
