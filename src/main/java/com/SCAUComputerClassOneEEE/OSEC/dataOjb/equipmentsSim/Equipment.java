package com.SCAUComputerClassOneEEE.OSEC.dataOjb.equipmentsSim;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.PCB;

import java.util.ArrayList;

public class Equipment {
    private static Equipment equipment = new Equipment();
    private static ArrayList<EAT> runningLists = new ArrayList<>();
    private static ArrayList<EAT> waitLists = new ArrayList<>();

    public void showEAT(){
        for (EAT eat:runningLists){
            System.out.println(eat.toString());
        }
    }

    public static Equipment getEquipment(){
        return equipment;
    }

    public void distributeEQ(char eqID,PCB pcb, int time){
        EAT eat = new EAT(eqID,pcb,time);
        if ((eqID=='A'&&getNumOf('A') < 2) || (eqID == 'B' && getNumOf('B') < 3) || (eqID == 'C' && getNumOf('C') < 3)){
            runningLists.add(eat);
        }
        else {
            waitLists.add(eat);
        }
        showEAT();
    }

    public static void decTime(){
        ArrayList<EAT> deleted = new ArrayList<>();
        ArrayList<EAT> buffer = new ArrayList<>();
        for(EAT each: runningLists){
            each.time = each.time - 1;
            if (each.time==0){
                //这两句可能有问题
                CPU.awake(each.pcb);

                deleted.add(each);

                EAT eat = canRun(each.eqID);//检查waitList里面有没有能够运行的，如果有么就返回EAT对象
                if (eat != null){
                    buffer.add(eat);
                }
            }
        }
        runningLists.addAll(buffer);
        runningLists.removeAll(deleted);

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

    class EAT{
        char eqID;
        PCB pcb;
        int time;

        public EAT(char eqID, PCB pcb, int time) {
            this.eqID = eqID;
            this.pcb = pcb;
            this.time = time;
        }

        @Override
        public String toString(){
            return "设备:"+this.eqID+" 进程ID:"+this.pcb.getProcessId()+" 剩余时间:"+this.time;
        }
    }
}
