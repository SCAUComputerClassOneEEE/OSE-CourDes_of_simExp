package com.SCAUComputerClassOneEEE.OSEC.utils;

import com.SCAUComputerClassOneEEE.OSEC.dataModel.devicesSim.Device;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.FileTree;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.Clock;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.dataService.DeviceSimService;
import com.SCAUComputerClassOneEEE.OSEC.dataService.DiskSimService;
import com.SCAUComputerClassOneEEE.OSEC.dataService.ProcessSimService;
import com.SCAUComputerClassOneEEE.OSEC.pane.DiskPane;
import com.SCAUComputerClassOneEEE.OSEC.pane.Terminal;

public class OS {
    public static CPU cpu = CPU.getCpu();
    public static Clock clock = Clock.getClock();
    public static Disk disk = Disk.getDisk();
    public static Memory memory = Memory.getMemory();
    public static Device device = Device.getDevice();
    public static FileTree fileTree = FileTree.getFileTree();

    public static DeviceSimService deviceSimService = DeviceSimService.getDeviceSimService();
    public static DiskSimService diskSimService = DiskSimService.getDiskSimService();
    public static ProcessSimService processSimService = ProcessSimService.getProcessSimService();

    public static DiskPane diskPane = DiskPane.getDiskPane();

    public static Terminal terminal = Terminal.getTerminal();

}
