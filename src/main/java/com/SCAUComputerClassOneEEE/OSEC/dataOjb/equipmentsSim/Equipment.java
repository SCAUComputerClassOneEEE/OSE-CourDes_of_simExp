package com.SCAUComputerClassOneEEE.OSEC.dataOjb.equipmentsSim;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.PCB;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.ProcessControlUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Equipment {
    private static Equipment equipment = new Equipment();
    public static ObservableList<EAT> runningLists = FXCollections.observableArrayList();
    private static ObservableList<EAT> waitLists = FXCollections.observableArrayList();

    public void showEAT(){
        System.out.println("\n-----------设备分配表------------");
        for (EAT eat:runningLists){
            System.out.println(eat.toString());
        }
        System.out.println("-----------设备分配表------------");
    }

    public static Equipment getEquipment(){
        return equipment;
    }

    public void distributeEQ(char eqID,PCB pcb, int time){
        EAT eat = new EAT(eqID,pcb,time);
        if ((eqID=='A'&&getNumOf('A') < 2) || (eqID == 'B' && getNumOf('B') < 3) || (eqID == 'C' && getNumOf('C') < 3)){
            runningLists.add(eat);
            pcb.setWaitEq("使用设备"+eqID);
        }
        else {
            waitLists.add(eat);
            pcb.setWaitEq("等待设备"+eqID);
        }

    }

    public static void decTime(){

        ObservableList<EAT> deleted = FXCollections.observableArrayList();
        ObservableList<EAT> needAdd = FXCollections.observableArrayList();
        for(EAT each: runningLists){
            each.time.setValue(each.time.getValue() - 1);
            if (each.time.get()<=0){

                ProcessControlUtil.awake(each.pcb);

                deleted.add(each);
                each.pcb.setWaitEq("无需等待设备");

                EAT eat = canRun(each.eqID);//检查waitList里面有没有能够运行的，如果有么就返回EAT对象
                if (eat != null){
                    needAdd.add(eat);
                    eat.pcb.setWaitEq("使用设备"+each.eqID);
                }
            }
        }
        runningLists.addAll(needAdd);
        waitLists.removeAll(needAdd);
        runningLists.removeAll(deleted);

        /*if (runningLists.size()>0)
            equipment.showEAT();*/

    }
    private static int getNumOf(char eqID){
        int count = 0;
        for(EAT each:runningLists){
            if (each.eqID==eqID)
                count++;
        }
        return count;
    }

    private static EAT canRun(char eqID){
        for (EAT eat:waitLists){
            if (eat.eqID==eqID){
                return eat;
            }
        }
        return null;
    }

}
