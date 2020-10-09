package com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim;

import lombok.Data;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author hlf
 * @date 25/8/2020
 */
@Data
public class Clock {
    private static long cpuRanTime = 0;
    private static long timeSlice = 0;

    public static void main(String[] args) throws InterruptedException {

        Thread t = new Thread(()->{
            Thread thread = Thread.currentThread();
            while(true){
                long sTime = System.currentTimeMillis();

                if (thread.isInterrupted()){
                    //
                    System.out.println("Running我被sky打断了");
                    break;
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    //
                    System.out.println("sleeping我被sky打断了");
                    break;
                }
                System.out.println(cpuRanTime);

                timeSlice += System.currentTimeMillis() - sTime;

                if (timeSlice >= 6000){
                    System.out.println("time over");
                    //通知进程调度器，时间片到了
                    timeSlice = 0;
                }

                cpuRanTime += System.currentTimeMillis() - sTime;
            }
        });
        t.start();

        Thread.sleep(12000);
        t.interrupt();
    }
}
