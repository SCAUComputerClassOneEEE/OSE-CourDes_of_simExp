package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.deviceModel;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.PCB;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Objects;

@Getter
@Setter
public class Device {
    private char type;              //类型
    private boolean occupy;         //是否被占据
    private PCB occupyPCB;          //占据进程
    private int remaining_time;     //剩余时间

    public Device(char type){
        this.type = type;
        this.occupy = false;
    }

    public boolean isOccupy(){
        return occupy;
    }

    public void occupy(PCB pcb, int remaining_time){
        this.occupyPCB = pcb;
        this.remaining_time = remaining_time;
    }

    public void free(){
        occupy(null,0);
        this.occupy = false;
    }

    /**
     * 设备分配表
     */
    @Getter
    @Setter
    public static class DeviceTable{
        static ArrayList<Device> device_allocation_table = new ArrayList<>();
        static ArrayList<PCB> waitListA = new ArrayList<>();
        static ArrayList<PCB> waitListB = new ArrayList<>();
        static ArrayList<PCB> waitListC = new ArrayList<>();

        public DeviceTable(){
            Device device1 = new Device('A');
            Device device2 = new Device('A');
            Device device3 = new Device('B');
            Device device4 = new Device('B');
            Device device5 = new Device('B');
            Device device6 = new Device('C');
            Device device7 = new Device('C');
            Device device8 = new Device('C');
            device_allocation_table.add(device1);
            device_allocation_table.add(device2);
            device_allocation_table.add(device3);
            device_allocation_table.add(device4);
            device_allocation_table.add(device5);
            device_allocation_table.add(device6);
            device_allocation_table.add(device7);
            device_allocation_table.add(device8);
        }

        /**
         * 申请设备
         * @param type 设备类型
         * @param pcb  进程
         * @return
         */
        public boolean occupyDevice(char type, PCB pcb){
            ArrayList<PCB> d = judgment(type);
            if(d == null)
                return false;
            else if(d.size() > 0){
                d.add(pcb);
                return false;
            }
            for (Device device : device_allocation_table)
                if (device.getType() == type && !device.isOccupy()){
                    device.occupy(pcb,5);
                    return true;
                }
            d.add(pcb);
            return false;
        }

        /**
         * 释放设备（还需要启动新进程）
         * @param type  设备类型
         * @param pcb   进程
         * @return
         */
        public boolean freeDevice(char type, PCB pcb){
            ArrayList<PCB> d = judgment(type);
            if(d == null)
                return false;
            for (Device device : device_allocation_table)
                if (device.getType() == type && device.isOccupy() && Objects.equals(device.getOccupyPCB(), pcb)){
                    if(d.size() > 0){
                        device.occupy(d.get(0),5);
                        d.remove(0);
                        //启动新进程
                    }else
                        device.free();
                    return true;
                }
            return false;
        }

        private ArrayList<PCB> judgment(char type){
            ArrayList<PCB> devices;
            switch (type){
                case 'A':
                    devices = waitListA;
                    break;
                case 'B':
                    devices = waitListB;
                    break;
                case 'C':
                    devices = waitListC;
                    break;
                default:
                    devices = null;
            }
            return devices;
        }
    }
}

