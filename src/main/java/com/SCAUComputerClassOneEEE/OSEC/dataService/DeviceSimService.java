package com.SCAUComputerClassOneEEE.OSEC.dataService;

import com.SCAUComputerClassOneEEE.OSEC.dataModel.devicesSim.EAT;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.devicesSim.device;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.PCB;

import java.util.ArrayList;

public class DeviceSimService {
    //设备服务层单例
    private static final DeviceSimService deviceSimService = new DeviceSimService();
    //缓冲链表，暂存需要增加或者删除的数据
    private static final ArrayList<EAT> needDelete = new ArrayList<>();
    private static final ArrayList<EAT> needAdd = new ArrayList<>();

    //获取单例
    public static DeviceSimService getDeviceSimService(){
        return DeviceSimService.deviceSimService;
    }

    //申请分配设备
    public void distributeDevice(char deviceID, PCB pcb, int requestTime){
        EAT eat = new EAT(deviceID,pcb,requestTime);
        //判断申请的设备是否可用
        if ((deviceID=='A'&&getNumOf('A') < 2) || (deviceID == 'B' && getNumOf('B') < 3) || (deviceID == 'C' && getNumOf('C') < 3)){
            device.getRunningLists().add(eat);
            pcb.setWaitEq("使用设备"+deviceID);
        }
        else {
            device.getWaitLists().add(eat);
            pcb.setWaitEq("等待设备"+deviceID);
        }

    }

    //对设备使用时间的更新
    public void decTime(){
        for(EAT each: device.getRunningLists()){
            each.remainingTime.setValue(each.remainingTime.getValue() - 1);
            if (each.remainingTime.get()<=0){

                ProcessSimService.getProcessSimService().awake(each.pcb);

                needDelete.add(each);
                each.pcb.setWaitEq("无需等待设备");

                EAT eat = canRun(each.deviceID);//检查waitList里面有没有能够运行的，如果有么就返回EAT对象
                if (eat != null){
                    needAdd.add(eat);
                    eat.pcb.setWaitEq("使用设备"+each.deviceID);
                }
            }
        }
        device.getRunningLists().addAll(needAdd);
        device.getWaitLists().removeAll(needAdd);
        device.getRunningLists().removeAll(needDelete);
    }

    //获取某一编号设备的总使用数
    private int getNumOf(char eqID){
        int count = 0;
        for(EAT each: device.getRunningLists()){
            if (each.deviceID ==eqID)
                count++;
        }
        return count;
    }

    //从等待队列中找到能够得到设备的进程
    private EAT canRun(char eqID){
        for (EAT eat: device.getWaitLists()){
            if (eat.deviceID ==eqID){
                return eat;
            }
        }
        return null;
    }

}
