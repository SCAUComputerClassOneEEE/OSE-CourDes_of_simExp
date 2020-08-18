package com.SCAUComputerClassOneEEE.OSEC.dataOjb.equipmentsSim;

public class EquipmentBusyException extends Exception{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public EquipmentBusyException(){
    };
    public String toString(){
        return "设备正在忙";
    }

}