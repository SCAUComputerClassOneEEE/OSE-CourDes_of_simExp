package com.SCAUComputerClassOneEEE.OSEC;

import com.SCAUComputerClassOneEEE.OSEC.dataModel.devicesSim.Device;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.FileModel.FileTree;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.Clock;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.dataService.DeviceSimService;
import com.SCAUComputerClassOneEEE.OSEC.dataService.DiskSimService;
import com.SCAUComputerClassOneEEE.OSEC.dataService.ProcessSimService;
import com.SCAUComputerClassOneEEE.OSEC.op.DiskPane;

public class OS {
    public static CPU cpu = CPU.getCpu();
    public static Clock clock = Clock.getClock();
    public static Disk disk = Disk.getDisk();
    public static Memory memory = Memory.getMemory();

    public static FileTree fileTree = FileTree.getFileTree();

    public static DeviceSimService deviceSimService = DeviceSimService.getDeviceSimService();
    //public static DiskSimService diskSimService = DiskSimService
    public static ProcessSimService processSimService = ProcessSimService.getProcessSimService();

    public static DiskPane diskPane = DiskPane.getDiskPane();


}
