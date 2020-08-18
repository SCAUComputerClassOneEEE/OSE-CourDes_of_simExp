package com.SCAUComputerClassOneEEE.OSEC.dataOjb.equipmentsSim;

import lombok.Getter;

@Getter
public class Equipment {
    private char name;
    private boolean isFree;
    private int workTime;

    public Equipment(char name, boolean isFree){
        this.name = name;
        this.isFree = true;
    }

    public void setIsFree(boolean isFree){
        this.isFree = isFree;
    }


    public void applyEquipment(int time) throws EquipmentBusyException {
        if(!isFree){
            throw new EquipmentBusyException();
        }else{
            this.isFree = false;
            this.workTime = time;
        }
    }

    public void descTime() throws EquipmentFreeException{
        this.workTime--;
        if(workTime == 0){
            this.isFree = true;
            throw new EquipmentFreeException();
        }
    }

}
