package com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim;

import javafx.beans.property.SimpleObjectProperty;
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

    private String exFileName;
    private int processId;//进程id
    private int pointerToMemory;//内存指针 0 ~ 511
    private int arriveTime;
    private SimpleObjectProperty<Integer> totalTime = new SimpleObjectProperty<>();
    private String waitEq;//正在等待的设备
    private int AX;//运行中x的值
    private int nextInstruction;//程序计数器

    private int remainInstructions;
    private int totalSize;

    private double progressRate;

    private Color color;

    public PCB(int pointerToMemory,int totalSize,Color color,int arriveTime,String exFileName){
        this.setAX(0);
        this.setNextInstruction(0);
        this.setPointerToMemory(pointerToMemory);//一开始不可用，创建时需要赋值
        this.setProcessId(PCB.nextProcessID++);//分配唯一的进程id,编号从零开始一直递增
        this.setWaitEq("无需等待设备");
        this.setTotalSize(totalSize);
        this.setColor(color);
        this.setArriveTime(arriveTime);
        this.totalTimeProperty().setValue(0);
        this.setExFileName(exFileName);
        this.setRemainInstructions(this.getTotalSize()-this.getNextInstruction());
        this.setProgressRate(((double)this.getTotalSize()-this.getRemainInstructions())/this.getTotalSize());
    }


    public int getTotalTime(){
        return totalTime.get();
    }

    public SimpleObjectProperty<Integer> totalTimeProperty() {
        return totalTime;
    }


}
