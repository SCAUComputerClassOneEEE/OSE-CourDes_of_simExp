package com.SCAUComputerClassOneEEE.OSEC.dataModel.devicesSim;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 设备管理实体
 * 对其操作位于DeviceService数据服务层
 */
public class Device {
    private static Device device = new Device();
    private static final ObservableList<EAT> runningLists = FXCollections.observableArrayList();
    private static final ObservableList<EAT> waitLists = FXCollections.observableArrayList();

    public static ObservableList<EAT> getRunningLists(){
        return Device.runningLists;
    }

    public static ObservableList<EAT> getWaitLists(){
        return Device.waitLists;
    }

    public static Device getDevice() {
        return device;
    }

    public void reset() {
        runningLists.clear();
        waitLists.clear();
    }
}
