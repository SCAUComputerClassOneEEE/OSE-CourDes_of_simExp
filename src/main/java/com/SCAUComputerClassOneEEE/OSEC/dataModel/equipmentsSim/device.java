package com.SCAUComputerClassOneEEE.OSEC.dataModel.equipmentsSim;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 设备管理实体
 * 对其操作位于DeviceService数据服务层
 */
public class device{
    private static ObservableList<EAT> runningLists = FXCollections.observableArrayList();
    private static ObservableList<EAT> waitLists = FXCollections.observableArrayList();

    public static ObservableList<EAT> getRunningLists(){
        return device.runningLists;
    }

    public static ObservableList<EAT> getWaitLists(){
        return device.waitLists;
    }

}
