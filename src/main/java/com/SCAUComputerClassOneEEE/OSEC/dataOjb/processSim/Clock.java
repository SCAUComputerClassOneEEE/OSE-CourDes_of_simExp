package com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author hlf
 * @date 25/8/2020
 */
public class Clock {
    public static void main(String[] args) {
        int stime = 0;
        int times = 6;
        while (true){
            System.out.println("系统时间"+stime+"："+"时间片时间"+times);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stime++;
            if(--times==0){
                times=6;
            }
        }
    }
}
