package com.SCAUComputerClassOneEEE.OSEC.data_center;

import com.SCAUComputerClassOneEEE.OSEC.data_model.devicesSim.Device;
import com.SCAUComputerClassOneEEE.OSEC.data_model.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.data_model.diskSim.FileTree;
import com.SCAUComputerClassOneEEE.OSEC.data_model.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.data_model.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.data_service.DeviceSimService;
import com.SCAUComputerClassOneEEE.OSEC.data_service.DiskSimService;
import com.SCAUComputerClassOneEEE.OSEC.data_service.ProcessSimService;
import com.SCAUComputerClassOneEEE.OSEC.pane.DiskPane;
import com.SCAUComputerClassOneEEE.OSEC.pane.Terminal;
import com.SCAUComputerClassOneEEE.OSEC.starter.Starter;

public class OSDataCenter {
    public static CPU cpu = CPU.getCpu();
    public static CPU.Clock clock = CPU.Clock.getClock();
    public static Disk disk = Disk.getDisk();
    public static Memory memory = Memory.getMemory();
    public static Device device = Device.getDevice();
    public static FileTree fileTree = FileTree.getFileTree();

    public static DeviceSimService deviceSimService = DeviceSimService.getDeviceSimService();
    public static DiskSimService diskSimService = DiskSimService.getDiskSimService();
    public static ProcessSimService processSimService = ProcessSimService.getProcessSimService();

    public static DiskPane diskPane = DiskPane.getDiskPane();

    public static Terminal terminal = Terminal.getTerminal();

    public static Starter starter = Starter.getStarter();

    public static double width = 1300;
    public static double height = 830;

}
