package com.SCAUComputerClassOneEEE.OSEC.dataModel.devicesSim;

import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.PCB;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @Author: Sky
 * @Date: 2020/10/16 12:21
 */
public class EAT{
    public char deviceID;//设备ID
    public int pcbID;//申请设备的进程ID
    public PCB pcb;//申请设备的进程控制块
    public SimpleObjectProperty<Integer> remainingTime = new SimpleObjectProperty<>();//剩余使用时间

    public EAT(char deviceID, PCB pcb, int time) {
        this.deviceID = deviceID;
        this.pcbID = pcb.getProcessId();
        this.pcb = pcb;
        this.remainingTime.setValue(time);
    }

    public int getRemainingTime(){
        return remainingTime.get();
    }

    public SimpleObjectProperty<Integer> remainingTimeProperty() {
        return remainingTime;
    }

    public char getDeviceID(){
        return deviceID;
    }

    public int getPcbID(){
        return pcbID;
    }

    @Override
    public String toString() {
        return "设备:" + deviceID + "进程ID:" + pcbID+ "剩余时间:" + remainingTime.get();
    }
}
