package com.SCAUComputerClassOneEEE.OSEC.dataOjb.equipmentsSim;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.PCB;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Sky
 * @Date: 2020/10/16 12:21
 */

public class EAT{
    public char eqID;
    public int pcbID;
    public PCB pcb;
    public SimpleObjectProperty<Integer> time = new SimpleObjectProperty<>();

    public EAT(char eqID, PCB pcb, int time) {
        this.eqID = eqID;
        this.pcb = pcb;
        this.time.setValue(time);
        this.pcbID = pcb.getProcessId();
    }

    public int getTime(){
        return time.get();
    }

    public SimpleObjectProperty<Integer> timeProperty() {
        return time;
    }

    public char getEqID(){
        return eqID;
    }

    public int getPcbID(){
        return pcbID;
    }
    @Override
    public String toString() {
        return "设备:"+eqID+"进程ID:"+pcbID+"剩余时间:"+time.get();
    }
}
