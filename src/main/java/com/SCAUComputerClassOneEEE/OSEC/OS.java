package com.SCAUComputerClassOneEEE.OSEC;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.equipmentsSim.Equipments;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.storageSim.MEM.Memory;

public class OS {
    public CPU cpu = CPU.getCpu();
    public Disk disk = Disk.getDisk();
    public Memory memory = Memory.getMemory();
    public Equipments equipments = Equipments.getEquipments();
}
