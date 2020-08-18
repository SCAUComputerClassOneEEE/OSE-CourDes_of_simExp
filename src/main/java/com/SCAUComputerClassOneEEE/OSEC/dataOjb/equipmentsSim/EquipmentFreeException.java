package com.SCAUComputerClassOneEEE.OSEC.dataOjb.equipmentsSim;

public class EquipmentFreeException extends Exception{


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    //private static final long uid=1L;
    public EquipmentFreeException(){};

    public String toString(){
        return "设备空闲";
    }

}
