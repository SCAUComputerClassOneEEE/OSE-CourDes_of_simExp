package com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.Date;

/**
 *
 *
 * pcb模拟
 * @author best lu
 * @since 2020/08/15
 */
@Getter
@Setter
public class PCB {
    private static int nextProcessID = 0;


    private int processId;//进程id
    private String processState;
    //private ProcessState processState;//进程状态
    private int pointerToMemory;//内存指针 0 ~ 512
    private Date timeWhenProcessStart;//启动时间
    //资源（占用设备、内存大小）

    private String waitEq;//正在等待的设备
    private int AX;//运行中x的值
    private int nextInstruction;//程序计数器

    private int totalSize;

    private Color color;

    public PCB(int pointerToMemory,int totalSize,Color color){
        this.setAX(0);
        this.setNextInstruction(0);
        this.setPointerToMemory(pointerToMemory);//一开始不可用，创建时需要赋值
        this.setProcessId(PCB.nextProcessID++);//分配唯一的进程id,编号从零开始一直递增
        this.setWaitEq("无需等待设备");
        this.setTotalSize(totalSize);
        this.setColor(color);
    }



}
